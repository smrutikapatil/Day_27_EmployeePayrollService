import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {
    Scanner SC = new Scanner(System.in);

    public enum IOService {CONSALE_IO, FILE_IO, DB_IO, REST_IO, ioType}

    private List<EmployeePayrollData> employeePayrollList;

    public EmployeePayrollService() {
        this.employeePayrollList = new ArrayList<EmployeePayrollData>();
    }

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
        this.employeePayrollList = employeePayrollList;
    }

    public static void main(String[] args) {
        ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        Scanner consoleInputReader = new Scanner(System.in);
        employeePayrollService.readEmployeePayrollData(consoleInputReader);
        employeePayrollService.writeEmployeePayrollData(IOService.ioType);
    }

    public void readEmployeePayrollData(Scanner consoleInputReader) {
        System.out.println("Enter Employee Id:");
        int employeeId = consoleInputReader.nextInt();
        System.out.println("Enter Employee Name:");
        String employeeName = consoleInputReader.next();
        System.out.println("Enter Employee Salary:");
        double employeeSalary = consoleInputReader.nextDouble();
        employeePayrollList.add(new EmployeePayrollData(employeeId, employeeName, employeeSalary));
    }

    public void writeEmployeePayrollData(IOService ioType) {
        if (ioType.equals(IOService.CONSALE_IO)) {
            for (EmployeePayrollData o : employeePayrollList)
                System.out.println(o.toString());
        } else if (ioType.equals(IOService.FILE_IO)) {
            new EmployeePayrollFileIOService().writeData(employeePayrollList);
        }
    }

    public long countEnteries(IOService ioType) {
        if (ioType.equals(IOService.FILE_IO))
            return new EmployeePayrollFileIOService().countEntries();
        return 0;
    }
}

