import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class Window {
    private JTextArea textArea;

    //HL-Dir
    private JButton hl_button = new JButton("Select Directory");
    private JLabel hl_label = new JLabel("No directory selected");

    //Resource-pack
    private JButton rp_button = new JButton("Select Directory");
    private JLabel rp_label = new JLabel("No directory selected");

    private JButton copyvert = new JButton("Convert");
    private JButton deploy = new JButton("Deploy");

    public Window(Install installer, ActionListener halflife, ActionListener resourcepack, ActionListener convert, ActionListener deployA) {
        // Create the JFrame
        SwingUtilities.invokeLater(() -> 
        {
            JFrame frame = new JFrame("Half-Craft Installer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLayout(new BorderLayout());
            
            // Create the JTextArea
            textArea = new JTextArea();
            textArea.setPreferredSize(new Dimension(400, 300));
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane, BorderLayout.CENTER);

            hl_button.addActionListener((e) -> 
            {
                enableAllButtons(false);
                halflife.actionPerformed(e);
                hl_label.setText(installer.hlLocation.toString());
                enableAllButtons(true);
                updateOutput("Half Life files are located in " + installer.hlLocation);
            });
            rp_button.addActionListener((e) -> {
                enableAllButtons(false);
                resourcepack.actionPerformed(e);
                rp_label.setText(installer.hcLocation.toString());
                enableAllButtons(true);
                updateOutput("Resource Packs are located in " + installer.hcLocation);
            });
            copyvert.addActionListener((e) -> {
                enableAllButtons(false);
                convert.actionPerformed(e);
                var currentDir = new File(System.getProperty("user.dir"));

                var hlSounds = new File(new File(installer.hlLocation, "valve"), "sound");
                var hlmusic = new File(new File(installer.hlLocation, "valve"), "media");

                var soundsFolder = new File(currentDir, "sounds");
                var musicFolder = new File(soundsFolder, "music");

                Converter.ProcessFolder(hlSounds.toPath(), soundsFolder.toPath(), this);
                Converter.ProcessFolder(hlmusic.toPath(), musicFolder.toPath(), this);
                enableAllButtons(true);
            });
            deploy.addActionListener((e) -> {
                enableAllButtons(false);
                deployA.actionPerformed(e);
                try {
                    updateOutput("Deploying '" + installer.projectDirectory.toPath() + "' to '" + installer.hcLocation.toPath() + "'");
                    Install.copyDirectory(installer.projectDirectory.toPath(), installer.hcLocation.toPath());
                    updateOutput("Finished Deploying!");
                } catch (IOException ex) {
                    updateOutput("Couldn't deploy!\n" + ex.getMessage());
                }
                enableAllButtons(true);
            });

            var grouping = new JPanel();
            grouping.setLayout(new GridLayout(6,1));

            grouping.add(hl_button);
            grouping.add(hl_label);

            grouping.add(rp_button);
            grouping.add(rp_label);

            grouping.add(copyvert);
            grouping.add(deploy);

            frame.add(grouping, BorderLayout.EAST);

            // Set the frame visibility
            frame.setVisible(true);
        });
    }

    private void enableAllButtons(boolean enable)
    {
        hl_button.setEnabled(enable);
        rp_button.setEnabled(enable);
        deploy.setEnabled(enable);
        copyvert.setEnabled(enable);
    }

    public void updateOutput(String line)
    {
        SwingUtilities.invokeLater(() -> 
        {
            textArea.append(line + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength()); // Scroll to the bottom
        });
    }
}
