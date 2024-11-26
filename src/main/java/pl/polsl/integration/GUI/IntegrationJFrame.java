package pl.polsl.integration.GUI;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import pl.polsl.integration.controller.IntegrationController;
import pl.polsl.integration.model.IntegrationException;
import pl.polsl.integration.model.IntegrationStrategyEnum;
import pl.polsl.integration.model.PairRecord;

/**
 * GUI class for handling user interaction and displaying the results of trapezoidal integration.
 * Provides a JFrame interface for setting parameters, performing integration, and viewing the results.
 * 
 * @version 3.0 final
 * 
 * @see IntegrationController
 * @see PairRecord
 * @see IntegrationException
 */
public class IntegrationJFrame extends javax.swing.JFrame {

    /** Object of controller class for managing the integration */
    private final IntegrationController integrationController = new IntegrationController();
        
    /**
    * Clears all rows from the result table, effectively emptying it.
    */
    public void clearResultTable() {
            DefaultTableModel model = (DefaultTableModel) jTableResult.getModel();
            model.setRowCount(0);
    }
    /**
     * Triggered when the calculate button is clicked.
     * Validates user inputs, sets the integration parameters, and calculates the result.
     * Displays the integration results and updates the result table in the GUI.
     * 
     * @param evt The mouse event triggered by clicking the button.
     */
    private void calculate()
    {
        integrationController.setMode(dModeRadio.isSelected() ? IntegrationStrategyEnum.DivisionsCount : IntegrationStrategyEnum.TrapesoidWidth);
        Boolean valid = true;

        try
        {
            integrationController.setDwvalue(dwInput.getText());
            dwError.setText("");
        }
        catch ( IntegrationException e )
        {
            dwError.setText(e.getMessage());
            valid = false;
        }

        try
        {
            integrationController.setStartValue(startInput.getText());
            startError.setText("");
        }
        catch ( IntegrationException e )
        {
            startError.setText(e.getMessage());
            valid = false;
        } 

        try
        {
            integrationController.setEndValue(endInput.getText());
            endError.setText("");
        }
        catch ( IntegrationException e )
        {
            endError.setText(e.getMessage());
            valid = false;
        }

        try
        {
            integrationController.setFunction(functionInput.getText());
            functionError.setText("");
        }
        catch ( IntegrationException e )
        {
            valid = false;
            functionError.setText(e.getMessage());
        }

        if(valid)
        {
            try
            {
                resultOutput.setText(integrationController.calculate());
                this.updateResultTable();
            }
            catch (IntegrationException ex)
            {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "There has been an issue with integrating your function, make sure all parameters are correct", JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            resultOutput.setText("Make sure all input fields are filled with valid data and try again...");
            this.clearResultTable();
        }
    }

    
    /**
     * Updates the result table to display the calculated (x, y) values from the integration process.
     * Retrieves the data from the controller's result table and populates the JTable with this data.
     */
    public void updateResultTable() {
        List<PairRecord> results = integrationController.getResultTable();

        DefaultTableModel model = (DefaultTableModel) jTableResult.getModel();
        model.setRowCount(0);

        for (PairRecord pair : results) {
            model.addRow(new Object[]{pair.x(), pair.y()});
        }


        double sumY = results.stream().mapToDouble(PairRecord::y).sum();

        model.addRow(new Object[]{
            "SUM * width",
            sumY * integrationController.getW()
        });

        model.addRow(new Object[]{
            "SUM * ( range / divisions )",
            sumY * (((Function<PairRecord, Double>) (a) -> a.y() - a.x()).apply(integrationController.getRange()) / integrationController.getD()) 
        });

        model.fireTableDataChanged();
    }
    
    /**
     * Sets the label for the DW input field based on the selected mode.
     * @param c The mode character ('d' for divisions, 'w' for width).
     */
    private void setMode(IntegrationStrategyEnum integrationStrategy)
    {
        switch (integrationStrategy) {
            case IntegrationStrategyEnum.DivisionsCount -> dwLabel.setText("Divisions count:");
            case IntegrationStrategyEnum.TrapesoidWidth -> dwLabel.setText("Trapezoid width:");
            default -> {
                dwLabel.setText("Unsupported mode?");
                JOptionPane.showMessageDialog(null, "Unsupported mode selected!", "", JOptionPane.ERROR_MESSAGE);
            }
        }
    }































    /**
     * Triggered when the "Divisions mode" radio button is selected.
     * Updates the label to indicate that the DW input represents "Divisions count."
     * @param evt
     */
    private void dModeRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dModeRadioActionPerformed
        this.setMode(IntegrationStrategyEnum.DivisionsCount);
    }//GEN-LAST:event_dModeRadioActionPerformed

    /**
     * Triggered when the "Width mode" radio button is selected.
     * Updates the label to indicate that the DW input represents "Trapezoid width."
     * @param evt
     */
    private void tModeRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tModeRadioActionPerformed
        this.setMode(IntegrationStrategyEnum.TrapesoidWidth);
    }//GEN-LAST:event_tModeRadioActionPerformed


    /**
     * Triggered when the "Divisions mode" radio button is selected.
     * Updates the label to indicate that the DW input represents "Divisions count."
     * 
     * @param evt The mouse event triggered by selecting the radio button.
     */
    private void dModeRadioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dModeRadioMouseClicked
        this.setMode(IntegrationStrategyEnum.DivisionsCount);
    }//GEN-LAST:event_dModeRadioMouseClicked

    /**
     * Triggered when the "Width mode" radio button is selected.
     * Updates the label to indicate that the DW input represents "Trapezoid width."
     * 
     * @param evt The mouse event triggered by selecting the radio button.
     */
    private void tModeRadioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tModeRadioMouseClicked
        this.setMode(IntegrationStrategyEnum.TrapesoidWidth);
    }//GEN-LAST:event_tModeRadioMouseClicked

    /**
     * The main method to launch the GUI application.
     * Sets the Nimbus look and feel and opens the IntegrationJFrame form.
     * 
     * @param args Command line arguments (not used in this version).
     */
    public static void start(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(IntegrationJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IntegrationJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IntegrationJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IntegrationJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>    
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IntegrationJFrame().setVisible(true);
            }
        });
    }
    private void calcualteButtonMouseClicked(java.awt.event.MouseEvent evt) {                                             
        this.calculate();
    }                                               
    
    /**
     * Creates new form IntegrationJFrame, initializing the JFrame components.
     */
    public IntegrationJFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dwButtonGroup = new javax.swing.ButtonGroup();
        resultOutput = new javax.swing.JTextField();
        functionLabel1 = new javax.swing.JLabel();
        calcualteButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        modeLabel = new javax.swing.JLabel();
        dModeRadio = new javax.swing.JRadioButton();
        tModeRadio = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        dwLabel = new javax.swing.JLabel();
        dwScrollPane = new javax.swing.JScrollPane();
        dwInput = new javax.swing.JTextPane();
        dwError = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        startLabel = new javax.swing.JLabel();
        startScrollPane = new javax.swing.JScrollPane();
        startInput = new javax.swing.JTextPane();
        startError = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        endError = new javax.swing.JLabel();
        endScrollPane = new javax.swing.JScrollPane();
        endInput = new javax.swing.JTextPane();
        endLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        functionLabel = new javax.swing.JLabel();
        functionScrollPane = new javax.swing.JScrollPane();
        functionInput = new javax.swing.JTextPane();
        functionError = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableResult = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        resultOutput.setEditable(false);
        resultOutput.setText("press Calcualte to see Results");
        resultOutput.setToolTipText("Result of the integration");
        resultOutput.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        functionLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        functionLabel1.setLabelFor(resultOutput);
        functionLabel1.setText("Results:");
        functionLabel1.setToolTipText("See the result of integration in the field on the right");

        calcualteButton.setMnemonic('c');
        calcualteButton.setText("Calculate");
        calcualteButton.setToolTipText("Run the program on given values and shw the result in the box on the left");
        calcualteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                calcualteButtonMouseClicked(evt);
            }
        });
        calcualteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcualteButtonActionPerformed(evt);
            }
        });

        modeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        modeLabel.setLabelFor(dModeRadio);
        modeLabel.setText("Iterate by:");
        modeLabel.setToolTipText("Choose one of the options on right to choose the iteration strategy for the integration");

        dwButtonGroup.add(dModeRadio);
        dModeRadio.setMnemonic('d');
        dModeRadio.setSelected(true);
        dModeRadio.setText("divisions count");
        dModeRadio.setToolTipText("Set to divisons count iteration mode");
        dModeRadio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dModeRadioMouseClicked(evt);
            }
        });
        dModeRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dModeRadioActionPerformed(evt);
            }
        });

        dwButtonGroup.add(tModeRadio);
        tModeRadio.setMnemonic('t');
        tModeRadio.setText("trapesoid width");
        tModeRadio.setToolTipText("Set to trapesoid width iteration mode");
        tModeRadio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tModeRadioMouseClicked(evt);
            }
        });
        tModeRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tModeRadioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(modeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dModeRadio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tModeRadio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(modeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(dModeRadio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tModeRadio)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dModeRadio.getAccessibleContext().setAccessibleName("Set the mode to divisions count");
        dModeRadio.getAccessibleContext().setAccessibleDescription("Set the mode to iterate thorugh the function by the amount of divisions.");
        tModeRadio.getAccessibleContext().setAccessibleName("Set the mode to trapesoid width");
        tModeRadio.getAccessibleContext().setAccessibleDescription("Set the mode to iterate thorugh the function by the set trapesoid width.");

        dwLabel.setDisplayedMnemonic('i');
        dwLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        dwLabel.setLabelFor(dwInput);
        dwLabel.setText("Divisions count:");
        dwLabel.setToolTipText("Enter Divisions Count or Trapesoid Width in the field on the right");

        dwInput.setContentType("number"); // NOI18N
        dwInput.setText("10");
        dwInput.setToolTipText("Divisions Count or Trapesoid Width, depending on selected mode. For Divisions Count value should be a positive integer. For Trapesoid Width value should be a positive number.");
        dwInput.setMinimumSize(new java.awt.Dimension(1, 1));
        dwScrollPane.setViewportView(dwInput);
        dwInput.getAccessibleContext().setAccessibleName("Iteration Input Panel");
        dwInput.getAccessibleContext().setAccessibleDescription("Enter the divvision count, or the trapesiod width, depending on selected mode. The value of division count mode should be a positive inteer, in trapesoid width mode it should be a positive number.");

        dwError.setForeground(new java.awt.Color(200, 10, 0));
        dwError.setLabelFor(dwInput);
        dwError.setText("field is empty!");
        dwError.setToolTipText("This error message reffers to the field Divisions count / Trapesoid width on the left");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dwLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dwScrollPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dwError)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dwLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(dwScrollPane)
            .addComponent(dwError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        dwLabel.getAccessibleContext().setAccessibleName("Divisions Count or Trapedois Width Label ");
        dwLabel.getAccessibleContext().setAccessibleDescription("");

        startLabel.setDisplayedMnemonic('l');
        startLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        startLabel.setLabelFor(startInput);
        startLabel.setText("Lower Boundry:");
        startLabel.setToolTipText("Enter Lower Boundry  into the field on the right");

        startInput.setContentType("number"); // NOI18N
        startInput.setText("1");
        startInput.setToolTipText("Bottom Boundry, value should be a number");
        startInput.setMinimumSize(new java.awt.Dimension(1, 1));
        startScrollPane.setViewportView(startInput);
        startInput.getAccessibleContext().setAccessibleName("Bottom bondry of the integration");
        startInput.getAccessibleContext().setAccessibleDescription("Enter the bottom boundry for integreation, the value should be a number");

        startError.setForeground(new java.awt.Color(200, 10, 0));
        startError.setLabelFor(startInput);
        startError.setText("field is empty!");
        startError.setToolTipText("This error message reffers to the field start on the left");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(startLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startScrollPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startError)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(startError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(startLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(startScrollPane)
        );

        startLabel.getAccessibleContext().setAccessibleName("Bottom Boundry Label");
        startLabel.getAccessibleContext().setAccessibleDescription("Label for Bottom Boundry Input Field");

        endError.setForeground(new java.awt.Color(200, 10, 0));
        endError.setLabelFor(endInput);
        endError.setText("field is empty!");
        endError.setToolTipText("This error message reffers to the field end on the left");

        endInput.setContentType("number"); // NOI18N
        endInput.setText("100");
        endInput.setToolTipText("Top Boundry, value should be a number");
        endInput.setMinimumSize(new java.awt.Dimension(1, 1));
        endScrollPane.setViewportView(endInput);
        endInput.getAccessibleContext().setAccessibleDescription("Enter the top boundry for integreation, the value should be a number");

        endLabel.setDisplayedMnemonic('u');
        endLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        endLabel.setLabelFor(endInput);
        endLabel.setText("Upper Boundry:");
        endLabel.setToolTipText("Enter the Upper Boundry to the field on the Right");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(endLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(endScrollPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(endError)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(endError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(endLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(endScrollPane)
        );

        endLabel.getAccessibleContext().setAccessibleName("Top Boundry Label");
        endLabel.getAccessibleContext().setAccessibleDescription("Label for Top Boundry Input Field");

        functionLabel.setDisplayedMnemonic('f');
        functionLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        functionLabel.setLabelFor(functionInput);
        functionLabel.setText("Function:");
        functionLabel.setToolTipText("Enter the functon to Integrate into the field on the right");

        functionInput.setText("2*x^2");
        functionInput.setToolTipText("Function formula, should be in format a*x^n+b*x^m...");
        functionInput.setMinimumSize(new java.awt.Dimension(1, 1));
        functionScrollPane.setViewportView(functionInput);
        functionInput.getAccessibleContext().setAccessibleName("Funciton Input Field");
        functionInput.getAccessibleContext().setAccessibleDescription("Enter the funcion formula, it should be in polynomial form of a*x^n+b*x^m ...");

        functionError.setForeground(new java.awt.Color(200, 10, 0));
        functionError.setLabelFor(functionInput);
        functionError.setText("field is empty!");
        functionError.setToolTipText("This error message reffers to the function start on the left");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(functionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(functionScrollPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(functionError)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(functionError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(functionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(functionScrollPane)
        );

        functionLabel.getAccessibleContext().setAccessibleName("Function Formula Label");
        functionLabel.getAccessibleContext().setAccessibleDescription("Label for Function Formula Input Field");

        jTableResult.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"0", "0"}
            },
            new String [] {
                "X", "Y"
            }
        ));
        jTableResult.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jScrollPane1.setViewportView(jTableResult);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(functionLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resultOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(calcualteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(functionLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resultOutput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(calcualteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addContainerGap())
        );

        resultOutput.getAccessibleContext().setAccessibleName("Result of the applicaiton");
        resultOutput.getAccessibleContext().setAccessibleDescription("Once application is run, the output is displayed inthis box.");
        functionLabel1.getAccessibleContext().setAccessibleDescription("Label for Result Box");
        calcualteButton.getAccessibleContext().setAccessibleName("Run the program");
        calcualteButton.getAccessibleContext().setAccessibleDescription("Clicking this button will run the program, using the values in form abov calculate the result and outputs it in the Result Box on the left");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void calcualteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcualteButtonActionPerformed
        this.calculate();
    }//GEN-LAST:event_calcualteButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton calcualteButton;
    private javax.swing.JRadioButton dModeRadio;
    private javax.swing.ButtonGroup dwButtonGroup;
    private javax.swing.JLabel dwError;
    private javax.swing.JTextPane dwInput;
    private javax.swing.JLabel dwLabel;
    private javax.swing.JScrollPane dwScrollPane;
    private javax.swing.JLabel endError;
    private javax.swing.JTextPane endInput;
    private javax.swing.JLabel endLabel;
    private javax.swing.JScrollPane endScrollPane;
    private javax.swing.JLabel functionError;
    private javax.swing.JTextPane functionInput;
    private javax.swing.JLabel functionLabel;
    private javax.swing.JLabel functionLabel1;
    private javax.swing.JScrollPane functionScrollPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableResult;
    private javax.swing.JLabel modeLabel;
    private javax.swing.JTextField resultOutput;
    private javax.swing.JLabel startError;
    private javax.swing.JTextPane startInput;
    private javax.swing.JLabel startLabel;
    private javax.swing.JScrollPane startScrollPane;
    private javax.swing.JRadioButton tModeRadio;
    // End of variables declaration//GEN-END:variables
}
