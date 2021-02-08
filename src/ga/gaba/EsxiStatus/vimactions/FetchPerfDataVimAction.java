package ga.gaba.EsxiStatus.vimactions;

import com.vmware.vim25.*;
import ga.gaba.EsxiManagementSdk.AbstractPropertyRetrievalVimAction;
import ga.gaba.EsxiStatus.models.PlotablePerfCounter;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by glyczak on 12/9/18.
 */
public class FetchPerfDataVimAction extends AbstractPropertyRetrievalVimAction {
    private List<PlotablePerfCounter> perfCounters;
    private PerfEntityMetric metric;
    private int utcOffset;
    private Second[] timeStamps = null;

    public FetchPerfDataVimAction(List<PlotablePerfCounter> perfCounters, int utcOffset) {
        super("192.168.1.4", "root", "dU2hy0hOs1oS7badS9", true);
        this.perfCounters = perfCounters;
        this.utcOffset = utcOffset;
    }

    private void generateTimeStamps() {
        if (timeStamps != null) return;
        timeStamps = new Second[metric.getSampleInfo().size()];
        for (int i = 0; i < timeStamps.length; i++) {
            ZonedDateTime time = metric.getSampleInfo().get(i).getTimestamp().toGregorianCalendar()
                    .toZonedDateTime().withZoneSameInstant(ZoneOffset.ofHours(utcOffset));
            timeStamps[i] = new Second(time.getSecond(), time.getMinute(), time.getHour(),
                    time.getDayOfMonth(), time.getMonthValue(), time.getYear());
        }
    }

    private PlotablePerfCounter getCounter(int id) {
        for (PlotablePerfCounter c :
                perfCounters) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public XYDataset getMetricDataset() {
        generateTimeStamps();
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (PerfMetricSeries generalSeries : metric.getValue()) {
            PerfMetricIntSeries intSeries = (PerfMetricIntSeries) generalSeries;
            List<Long> values = intSeries.getValue();
            PlotablePerfCounter counter = getCounter(intSeries.getId().getCounterId());
            TimeSeries tSeries = new TimeSeries(counter.toString());
            for (int i = 0; i < timeStamps.length ; i++) {
                tSeries.add(timeStamps[i], values.get(i) * counter.getMultiplier());
            }
            dataset.addSeries(tSeries);
        }
        return dataset;
    }

    protected void run() throws Exception {
        List<PerfQuerySpec> pqSpecSet = new ArrayList<>(1);
        pqSpecSet.add(new PerfQuerySpec());
        PerfQuerySpec pqSpec = pqSpecSet.get(0);

        List<ObjectContent> hosts = retrieveFromRoot("HostSystem", new ArrayList<String>());
        pqSpec.setEntity(hosts.get(0).getObj());

        for (PlotablePerfCounter counter : perfCounters) {
            PerfMetricId pmId = new PerfMetricId();
            pmId.setCounterId(counter.getId());
            pmId.setInstance("");
            pqSpec.getMetricId().add(pmId);
        }

        List<PerfEntityMetricBase> metrics = vim.queryPerf(sc.getPerfManager(), pqSpecSet);

        metric = (PerfEntityMetric) metrics.get(0);
    }
}
