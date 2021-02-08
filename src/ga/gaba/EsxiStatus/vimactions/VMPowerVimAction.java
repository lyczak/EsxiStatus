package ga.gaba.EsxiStatus.vimactions;

import com.vmware.vim25.*;
import ga.gaba.EsxiManagementSdk.AbstractVimAction;
import ga.gaba.EsxiStatus.EsxiCredentials;

/**
 * Created by glyczak on 3/31/19.
 */
public class VMPowerVimAction extends AbstractVimAction {
    private ManagedObjectReference vm;
    private ManagedObjectReference task;
    private VirtualMachinePowerState setTo;

    public VMPowerVimAction(ManagedObjectReference vm, VirtualMachinePowerState setTo) {
        super(EsxiCredentials.getServer(), EsxiCredentials.getUser(), EsxiCredentials.getPassword(), true);

        this.vm = vm;
        this.setTo = setTo;
    }

    protected ManagedObjectReference getTask() {
        return task;
    }

    protected void run() throws FileFaultFaultMsg, InsufficientResourcesFaultFaultMsg, InvalidStateFaultMsg,
            RuntimeFaultFaultMsg, TaskInProgressFaultMsg, VmConfigFaultFaultMsg {
        switch (setTo) {
            case POWERED_ON:
                task = vim.powerOnVMTask(vm, null);
                break;
            case POWERED_OFF:
                task = vim.powerOffVMTask(vm);
                break;
            case SUSPENDED:
                task = vim.suspendVMTask(vm);
                break;
        }
    }
}
