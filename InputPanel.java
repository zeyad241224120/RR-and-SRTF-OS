import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InputPanel extends JPanel {

    
    private final List<Process>      processList     = new ArrayList<>();
    private final DefaultTableModel  inputTableModel;

    public  final JTextField pidField;
    public  final JTextField atField;
    public  final JTextField btField;
    public  final JTextField quantumField;

    private final Runnable onSimulate;
    
    //  Constructor
    
    public InputPanel(Runnable onSimulate) {
        this.onSimulate = onSimulate;
        setLayout(new BorderLayout());

        
        pidField     = new JTextField(5);
        atField      = new JTextField(5);
        btField      = new JTextField(5);
        quantumField = new JTextField("2", 3);

        JPanel fields = new JPanel(new FlowLayout());
        fields.setBorder(BorderFactory.createTitledBorder("Add Process"));

        fields.add(new JLabel("PID:"));     fields.add(pidField);
        fields.add(new JLabel("Arrival:")); fields.add(atField);
        fields.add(new JLabel("Burst:"));   fields.add(btField);
        fields.add(new JLabel("Quantum:")); fields.add(quantumField);

        JButton addButton   = new JButton("Add Process");
        JButton clearButton = new JButton("Clear All");
        JButton simButton   = new JButton("▶ Simulate Both");
        simButton.setBackground(new Color(46, 204, 113));
        simButton.setForeground(Color.WHITE);

        fields.add(addButton);
        fields.add(clearButton);
        fields.add(simButton);

        JPanel scenarioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        scenarioPanel.setBorder(BorderFactory.createTitledBorder("Required Test Scenarios (A-E)"));
        String[] labels = {"A: Basic Mixed", "B: Quantum Sensitivity",
                           "C: Short-job Heavy", "D: Interactive Fairness", "E: Validation Case"};
        for (String label : labels) {
            JButton btn = new JButton(label);
            btn.addActionListener(e -> handleScenario(label.split(":")[0].trim()));
            scenarioPanel.add(btn);
        }

        inputTableModel = new DefaultTableModel(
        new String[]{"PID", "Arrival Time", "Burst Time"}, 0) {

       @Override
       public boolean isCellEditable(int row, int column) {
           return false;
         }
       };

        addButton.addActionListener(e -> addProcess());
        clearButton.addActionListener(e -> {
            processList.clear();
            inputTableModel.setRowCount(0);
        });
        simButton.addActionListener(e -> onSimulate.run());

        add(fields,        BorderLayout.NORTH);
        add(scenarioPanel, BorderLayout.SOUTH);
    }

    public void addProcess() {
        try {
            String id = pidField.getText().trim();

            if (id.isEmpty())
                throw new Exception("PID cannot be empty.");

            for (Process p : processList) {
                if (p.id.equalsIgnoreCase(id))
                    throw new Exception("PID already exists. Use a unique PID.");
            }

            int at = Integer.parseInt(atField.getText().trim());
            int bt = Integer.parseInt(btField.getText().trim());

            if (at < 0)
                throw new Exception("Arrival Time must be >= 0.");
            if (bt <= 0)
                throw new Exception("Burst Time must be > 0.");

            processList.add(new Process(id, at, bt));
            inputTableModel.addRow(new Object[]{id, at, bt});

            pidField.setText("");
            atField.setText("");
            btField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Invalid input! Arrival Time and Burst Time must be numeric integers.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    public void handleScenario(String type) {
        processList.clear();
        inputTableModel.setRowCount(0);

        switch (type) {
            case "A": load(new Object[][]{{"P1",0,8},{"P2",1,4},{"P3",2,9},{"P4",3,5}}, "3"); break;
            case "B": load(new Object[][]{{"P1",0,20},{"P2",0,20}}, "10");                     break;
            case "C": load(new Object[][]{{"P1",0,20},{"P2",1,2},{"P3",2,2}}, "2");            break;
            case "D": load(new Object[][]{{"P1",0,10},{"P2",0,10},{"P3",0,10}}, "1");          break;

            case "E":
                JOptionPane.showMessageDialog(this,
                    "Scenario E — Validation Demo\n\nThree invalid entries will be attempted:\n" +
                    "  1) Negative Arrival Time  (-5)\n" +
                    "  2) Zero Burst Time  (0)\n" +
                    "  3) Empty PID\n\n" +
                    "Each will raise an error. Then a valid process is added to confirm recovery.",
                    "Scenario E: Validation Case", JOptionPane.INFORMATION_MESSAGE);

                pidField.setText("ErrP1"); atField.setText("-5");  btField.setText("5"); addProcess();
                pidField.setText("ErrP2"); atField.setText("0");   btField.setText("0"); addProcess();
                pidField.setText("");      atField.setText("1");   btField.setText("3"); addProcess();
                pidField.setText("ValidP");atField.setText("0");   btField.setText("5"); addProcess();
                return;
        }
        onSimulate.run();
    }

    
    private void load(Object[][] data, String q) {
        quantumField.setText(q);
        for (Object[] row : data) {
            processList.add(new Process((String) row[0], (int) row[1], (int) row[2]));
            inputTableModel.addRow(row);
        }
    }

    
    public List<Process>     getProcessList()    { return processList;     }
    public DefaultTableModel getTableModel()     { return inputTableModel; }
    public String            getQuantumText()    { return quantumField.getText().trim(); }
}
