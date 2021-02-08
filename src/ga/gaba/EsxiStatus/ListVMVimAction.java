package ga.gaba.EsxiStatus;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import ga.gaba.EsxiManagementSdk.AbstractPropertyRetrievalVimAction;
import ga.gaba.EsxiManagementSdk.DynamicObjectContent;
import ga.gaba.EsxiManagementSdk.samples.ListVmVimAction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by glyczak on 12/8/18.
 */
public class ListVMVimAction extends AbstractPropertyRetrievalVimAction {
    private List<DynamicObjectContent> vms = new ArrayList<>();

    public ListVMVimAction(String server, String username, String password,
                              boolean skipServerVerification) {
        super(server, username, password, skipServerVerification);
    }

    public ListVMVimAction() {
        super(EsxiCredentials.getServer(), EsxiCredentials.getUser(), EsxiCredentials.getPassword(), true);
    }

    protected void run() throws Exception {
        List<String> props = new ArrayList<>();
        props.add("name");
        props.add("summary.runtime");

        List<ObjectContent> objects = retrieveFromRoot("VirtualMachine", props);

        for (ObjectContent content : objects) {
            vms.add(new DynamicObjectContent(content));
        }
    }

    public List<DynamicObjectContent> getVMContent() {
        return vms;
    }

    public List<ManagedObjectReference> getVMReferences() {
        return vms.stream().map(DynamicObjectContent::getObj)
                .collect(Collectors.toList());
    }

    public List<String> getPoweredVMNames() {
        List<String> vmNames = new ArrayList<>(vms.size());
        for (DynamicObjectContent vm :
                vms) {
            try {
                VirtualMachineRuntimeInfo rti = vm.getProp("summary.runtime");
                if ("poweredOn".equals(rti.getPowerState().value())) {
                    String name = vm.getProp("name");
                    vmNames.add(name);
                }
            } catch (Exception e) {}
        }
        return vmNames;
    }

    public List<String> getVMNames() throws Exception {
        List<String> vmNames = new ArrayList<>(vms.size());
        for (DynamicObjectContent vm :
                vms) {
            vmNames.add(vm.getProp("name"));
            try {

            } catch (Exception e) {}
        }
        return vmNames;
    }
}
