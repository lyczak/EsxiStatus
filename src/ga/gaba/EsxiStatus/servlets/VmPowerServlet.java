package ga.gaba.EsxiStatus.servlets;

import com.vmware.vim25.InvalidStateFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.TaskInProgressFaultMsg;
import com.vmware.vim25.VirtualMachinePowerState;
import ga.gaba.EsxiStatus.vimactions.VMPowerVimAction;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by glyczak on 3/30/19.
 */
public class VmPowerServlet extends HttpServlet {
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

    }

    /*
    * POST /power
    * vm = value ManagedObjectReference to a VirtualMachine
    * state = name of a valid VirtualMachinePowerState
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        VirtualMachinePowerState powerState;
        try {
            powerState = VirtualMachinePowerState
                    .fromValue(request.getParameter("state"));
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "The provided power state is not in the list of acceptable states.");
            return;
        }

        ManagedObjectReference vm = new ManagedObjectReference();
        vm.setType("VirtualMachine");
        vm.setValue(request.getParameter("vm"));

        VMPowerVimAction action = new VMPowerVimAction(vm, powerState);
        try {
            action.execute();
        } catch (InvalidStateFaultMsg e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Virtual machine could not be set to that state from the current one.");
        } catch (TaskInProgressFaultMsg e) {
            response.sendError(423,
                    "A power state task is already in progress.");
        } catch (Exception e) {
            throw new ServletException(e);
        }

        String redirect = request.getParameter("redirect");
        if (redirect != null) {
            response.sendRedirect(redirect);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
