package ga.gaba.EsxiStatus.servlets;

import ga.gaba.EsxiStatus.models.PlotablePerfCounter;
import ga.gaba.EsxiStatus.vimactions.FetchPerfDataVimAction;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 * Created by glyczak on 12/9/18.
 */
public class PerfChartServlet extends HttpServlet {
    private List<PlotablePerfCounter> plotablePerfCounters;

    private PlotablePerfCounter getCounter(int id) {
        for (PlotablePerfCounter c : plotablePerfCounters) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String[] ppcStrs = config.getInitParameter("plotablePerfCounters").split(";");
        plotablePerfCounters = new ArrayList<>(ppcStrs.length);
        for (String ppcParamsStr :
                ppcStrs) {
            String[] ppcParams = ppcParamsStr.split(",");
            Integer id = Integer.parseInt(ppcParams[0]);
            if (ppcParams.length == 3) {
                plotablePerfCounters.add(new PlotablePerfCounter(id, ppcParams[1], ppcParams[2]));
            } else {
                Double mult = Double.parseDouble(ppcParams[3]);
                plotablePerfCounters.add(new PlotablePerfCounter(id, ppcParams[1], ppcParams[2], mult));
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Color bgColor;
        try {
             bgColor = Color.decode("#" + request.getParameter("color"));
        } catch (NumberFormatException e) {
            bgColor = new Color(198, 231, 255);
        }

        Integer width, height;
        try {
            width = Integer.parseInt(request.getParameter("width"));
            height = Integer.parseInt(request.getParameter("height"));
        } catch (NumberFormatException e) {
            width = 800;
            height = 400;
        }

        PlotablePerfCounter counter;
        try {
            counter = getCounter(Integer.parseInt(request.getParameter("id")));
        } catch (Exception e) {
            throw new ServletException(e);
        }

        List<PlotablePerfCounter> counters = new ArrayList<>(1);
        counters.add(counter);

        FetchPerfDataVimAction action = new FetchPerfDataVimAction(counters, -5);
        try {
            action.execute();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        XYDataset dataset = action.getMetricDataset();
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                counter.getName() + " over Time",
                "Time",
                counter.toString(),
                dataset);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, bgColor.darker().darker());
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        //Changes background color
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(bgColor);

        response.setContentType("image/png");
        ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, width, height);
    }
}
