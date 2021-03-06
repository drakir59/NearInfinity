// Near Infinity - An Infinity Engine Browser and Editor
// Copyright (C) 2001 - 2005 Jon Olav Hauglid
// See LICENSE.txt for license information

package org.infinity.gui.converter;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import org.infinity.gui.ButtonPopupMenu;
import org.infinity.gui.ViewerUtil;
import org.infinity.resource.Profile;
import org.infinity.util.io.FileManager;

/**
 * An options dialog for the BAM converter.
 */
class BamOptionsDialog extends JDialog implements ActionListener, FocusListener
{
  private static final String PREFS_BAMVERSION    = "BCBamVersion";
  private static final String PREFS_PATH          = "BCPath";
  private static final String PREFS_AUTOCLEAR     = "BCAutoClear";
  private static final String PREFS_CLOSEONEXIT   = "BCCloseOnExit";
  private static final String PREFS_TRANSPARENCY  = "BCTransparencyThreshold";
  private static final String PREFS_COMPRESSBAM   = "BCCompressBam";
  private static final String PREFS_COMPRESSTYPE  = "BCCompressionType";
  private static final String PREFS_PVRZINDEX     = "BCPvrzIndex";

  // Default settings
  private static final int DEFAULT_BAM_VERSION            = ConvertToBam.VERSION_BAMV1;
  private static final String DEFAULT_PATH                = "";
  private static final boolean DEFAULT_AUTO_CLEAR         = true;
  private static final boolean DEFAULT_CLOSE_ON_EXIT      = false;
  private static final int DEFAULT_TRANSPARENCY_THRESHOLD = 5;    // in percent
  private static final boolean DEFAULT_COMPRESS_BAM       = false;
  private static final int DEFAULT_COMPRESSION_TYPE       = ConvertToBam.COMPRESSION_AUTO;
  private static final int DEFAULT_PVRZ_INDEX             = 1000;

  // Current settings
  private static boolean settingsLoaded     = false;
  private static int bamVersion             = DEFAULT_BAM_VERSION;
  private static String path                = DEFAULT_PATH;
  private static boolean autoClear          = DEFAULT_AUTO_CLEAR;
  private static boolean closeOnExit        = DEFAULT_CLOSE_ON_EXIT;
  private static int transparencyThreshold  = DEFAULT_TRANSPARENCY_THRESHOLD;
  private static boolean compressBam        = DEFAULT_COMPRESS_BAM;
  private static int compressionType        = DEFAULT_COMPRESSION_TYPE;
  private static int pvrzIndex              = DEFAULT_PVRZ_INDEX;

  private JButton bOK, bCancel, bDefaults, bTransparencyHelp;
  private JComboBox<String> cbBamVersion, cbCompressionType;
  private JCheckBox cbCloseOnExit, cbAutoClear, cbCompressBam;
  private JSpinner sTransparency, sPvrzIndex;
  private JTextField tfPath;
  private JMenuItem miPathSet, miPathClear;
  private ButtonPopupMenu bpmPath;


  /** Attempts to load stored settings from disk. Falls back to default values. */
  public static void loadSettings(boolean force)
  {
    if (!settingsLoaded || force) {
      Preferences prefs = Preferences.userNodeForPackage(ConvertToBam.class);

      bamVersion = prefs.getInt(PREFS_BAMVERSION, DEFAULT_BAM_VERSION);
      path = prefs.get(PREFS_PATH, DEFAULT_PATH);
      autoClear = prefs.getBoolean(PREFS_AUTOCLEAR, DEFAULT_AUTO_CLEAR);
      closeOnExit = prefs.getBoolean(PREFS_CLOSEONEXIT, DEFAULT_CLOSE_ON_EXIT);
      transparencyThreshold = prefs.getInt(PREFS_TRANSPARENCY, DEFAULT_TRANSPARENCY_THRESHOLD);
      compressBam = prefs.getBoolean(PREFS_COMPRESSBAM, DEFAULT_COMPRESS_BAM);
      compressionType = prefs.getInt(PREFS_COMPRESSTYPE, DEFAULT_COMPRESSION_TYPE);
      pvrzIndex = prefs.getInt(PREFS_PVRZINDEX, DEFAULT_PVRZ_INDEX);

      validateSettings();
      settingsLoaded = true;
    }
  }

  /** Stores the current settings on disk. */
  public static void saveSettings()
  {
    validateSettings();
    Preferences prefs = Preferences.userNodeForPackage(ConvertToBam.class);

    prefs.putInt(PREFS_BAMVERSION, bamVersion);
    prefs.put(PREFS_PATH, path);
    prefs.putBoolean(PREFS_AUTOCLEAR, autoClear);
    prefs.putBoolean(PREFS_CLOSEONEXIT, closeOnExit);
    prefs.putInt(PREFS_TRANSPARENCY, transparencyThreshold);
    prefs.putBoolean(PREFS_COMPRESSBAM, compressBam);
    prefs.putInt(PREFS_COMPRESSTYPE, compressionType);
    prefs.putInt(PREFS_PVRZINDEX, pvrzIndex);
  }

  // Makes sure that all settings are valid.
  private static void validateSettings()
  {
    bamVersion = Math.min(Math.max(bamVersion, ConvertToBam.VERSION_BAMV1), ConvertToBam.VERSION_BAMV2);
    if (path == null) path = DEFAULT_PATH;
    if (!path.isEmpty() && !(Files.isDirectory(FileManager.resolve(path)))) path = DEFAULT_PATH;
    transparencyThreshold = Math.min(Math.max(transparencyThreshold, 0), 100);
    compressionType = Math.min(Math.max(compressionType, ConvertToBam.COMPRESSION_AUTO), ConvertToBam.COMPRESSION_DXT5);
    pvrzIndex = Math.min(Math.max(pvrzIndex, 0), 99999);
  }

  /** Returns the default BAM version index. */
  public static int getBamVersion() { return bamVersion; }
  /** Returns the default path. */
  public static String getPath() { return path; }
  /** Returns whether to automatically clear the current BAM after a successful conversion. */
  public static boolean getAutoClear() { return autoClear; }
  /** Returns the default state for the "Close On Exit" checkbox. */
  public static boolean getCloseOnExit() { return closeOnExit; }
  /** Returns the transparency threshold in percent. */
  public static int getTransparencyThreshold() { return transparencyThreshold; }
  /** Returns the default state for "Compress BAM" checkbox (BAM v1). */
  public static boolean getCompressBam() { return compressBam; }
  /** Returns the default compression type (BAM v2). */
  public static int getCompressionType() { return compressionType; }
  /** Returns the default PVRZ index (BAM v2). */
  public static int getPvrzIndex() { return pvrzIndex; }


  public BamOptionsDialog(ConvertToBam parent)
  {
    super(parent, "Options", Dialog.ModalityType.DOCUMENT_MODAL);
    if (parent == null) {
      throw new NullPointerException();
    }
    init();
  }

//--------------------- Begin Interface ActionListener ---------------------

  @Override
  public void actionPerformed(ActionEvent event)
  {
    if (event.getSource() == miPathSet) {
      Path path = FileManager.resolve(tfPath.getText());
      if (!Files.isDirectory(path)) {
        path = Profile.getGameRoot();
      }
      Path rootPath = ConvertToBam.getOpenPathName(this, "Select initial directory", path);
      if (rootPath != null) {
        tfPath.setText(rootPath.toString());
      }
    } else if (event.getSource() == miPathClear) {
      tfPath.setText("");
    } else if (event.getSource() == bTransparencyHelp) {
      String msg = "Defines a threshold that is used to determine whether a color is considered \"transparent\".\n\n" +
                   "Example:\n" +
                   "A value of 5% will treat any pixels with a transparency of 5% or higher as fully transparent.\n" +
                   "Pixels with less than 5% transparency will be treated as fully opaque.\n\n" +
                   "Note: This setting only affects Legacy BAM (v1) conversions.";
      JOptionPane.showMessageDialog(this, msg, "Help", JOptionPane.INFORMATION_MESSAGE);
    } else if (event.getSource() == bDefaults) {
      setDefaults();
    } else if (event.getSource() == bOK) {
      updateSettings();
      saveSettings();
      setVisible(false);
    } else if (event.getSource() == bCancel) {
      setVisible(false);
    }
  }

//--------------------- End Interface ActionListener ---------------------

//--------------------- Begin Interface FocusListener ---------------------

  @Override
  public void focusGained(FocusEvent event)
  {
  }

  @Override
  public void focusLost(FocusEvent event)
  {
    if (event.getSource() == tfPath) {
      String path = tfPath.getText();
      if (!path.isEmpty() && !Files.isDirectory(FileManager.resolve(path))) {
        tfPath.setText(path);
      }
    }
  }

//--------------------- End Interface FocusListener ---------------------

  private void init()
  {
    loadSettings(false);

    GridBagConstraints c = new GridBagConstraints();

    // initializing "General" panel
    JLabel l1 = new JLabel("Default BAM version:");
    JLabel l2 = new JLabel("Default root path:");
    cbBamVersion = new JComboBox<>(ConvertToBam.BamVersionItems);
    cbBamVersion.setSelectedIndex(getBamVersion());
    tfPath = new JTextField();
    tfPath.setText(getPath());
    tfPath.addFocusListener(this);
    miPathSet = new JMenuItem("Set...");
    miPathSet.addActionListener(this);
    miPathClear = new JMenuItem("Clear");
    miPathClear.addActionListener(this);
    bpmPath = new ButtonPopupMenu("...", new JMenuItem[]{miPathSet, miPathClear});
    cbCloseOnExit = new JCheckBox("Select \"Close dialog after conversion\" by default", getCloseOnExit());
    cbAutoClear = new JCheckBox("Automatically clear frames and cycles after conversion", getAutoClear());

    JPanel pGeneral = new JPanel(new GridBagLayout());
    pGeneral.setBorder(BorderFactory.createTitledBorder("General "));
    c = ViewerUtil.setGBC(c, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0);
    pGeneral.add(l1, c);
    c = ViewerUtil.setGBC(c, 1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.NONE, new Insets(0, 4, 0, 4), 0, 0);
    pGeneral.add(cbBamVersion, c);
    c = ViewerUtil.setGBC(c, 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.NONE, new Insets(4, 4, 0, 0), 0, 0);
    pGeneral.add(l2, c);
    c = ViewerUtil.setGBC(c, 1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.HORIZONTAL, new Insets(4, 4, 0, 0), 0, 0);
    pGeneral.add(tfPath, c);
    c = ViewerUtil.setGBC(c, 2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.NONE, new Insets(4, 4, 0, 4), 0, 0);
    pGeneral.add(bpmPath, c);
    c = ViewerUtil.setGBC(c, 0, 2, 3, 1, 1.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.NONE, new Insets(4, 4, 0, 4), 0, 0);
    pGeneral.add(cbAutoClear, c);
    c = ViewerUtil.setGBC(c, 0, 3, 3, 1, 1.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0);
    pGeneral.add(cbCloseOnExit, c);

    // initializing "Legacy BAM" panel
    l1 = new JLabel("Transparency threshold:");
    SpinnerNumberModel model = new SpinnerNumberModel(getTransparencyThreshold(), 0, 100, 1);
    sTransparency = new JSpinner(model);
    sTransparency.setEditor(new JSpinner.NumberEditor(sTransparency, "#'%'"));
    bTransparencyHelp = new JButton("?");
    bTransparencyHelp.setMargin(new Insets(2, 4, 2, 4));
    bTransparencyHelp.setToolTipText("About transparency threshold");
    bTransparencyHelp.addActionListener(this);
    cbCompressBam = new JCheckBox("Select \"Compress BAM\" by default", getCompressBam());
    JPanel pBamV1 = new JPanel(new GridBagLayout());
    pBamV1.setBorder(BorderFactory.createTitledBorder("Legacy BAM "));
    c = ViewerUtil.setGBC(c, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0);
    pBamV1.add(l1, c);
    c = ViewerUtil.setGBC(c, 1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.HORIZONTAL, new Insets(0, 4, 0, 0), 0, 0);
    pBamV1.add(sTransparency, c);
    c = ViewerUtil.setGBC(c, 2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.NONE, new Insets(0, 4, 0, 4), 0, 0);
    pBamV1.add(bTransparencyHelp, c);
    c = ViewerUtil.setGBC(c, 0, 1, 3, 1, 1.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0);
    pBamV1.add(cbCompressBam, c);

    // initializing "PVRZ-based BAM" panel
    l1 = new JLabel("Default compression:");
    l2 = new JLabel("Default PVRZ index:");
    cbCompressionType = new JComboBox<>(ConvertToBam.CompressionItems);
    cbCompressionType.setSelectedIndex(getCompressionType());
    model = new SpinnerNumberModel(getPvrzIndex(), 0, 99999, 1);
    sPvrzIndex = new JSpinner(model);
    JPanel pBamV2 = new JPanel(new GridBagLayout());
    pBamV2.setBorder(BorderFactory.createTitledBorder("PVRZ-based BAM "));
    c = ViewerUtil.setGBC(c, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0);
    pBamV2.add(l1, c);
    c = ViewerUtil.setGBC(c, 1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.HORIZONTAL, new Insets(0, 4, 0, 0), 0, 0);
    pBamV2.add(cbCompressionType, c);
    c = ViewerUtil.setGBC(c, 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.NONE, new Insets(4, 4, 4, 0), 0, 0);
    pBamV2.add(l2, c);
    c = ViewerUtil.setGBC(c, 1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0);
    pBamV2.add(sPvrzIndex, c);

    // initializing bottom button bar
    bDefaults = new JButton("Set defaults");
    bDefaults.addActionListener(this);
    bDefaults.setMargin(new Insets(4, bDefaults.getInsets().left, 4, bDefaults.getInsets().right));
    bCancel = new JButton("Cancel");
    bCancel.addActionListener(this);
    bCancel.setMargin(new Insets(4, bCancel.getInsets().left, 4, bCancel.getInsets().right));
    bOK = new JButton("OK");
    bOK.addActionListener(this);
    bOK.setMargin(new Insets(4, bOK.getInsets().left, 4, bOK.getInsets().right));
    bOK.setPreferredSize(bCancel.getPreferredSize());
    JPanel pButtons = new JPanel(new GridBagLayout());
    c = ViewerUtil.setGBC(c, 0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
    pButtons.add(bDefaults, c);
    c = ViewerUtil.setGBC(c, 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0);
    pButtons.add(bOK, c);
    c = ViewerUtil.setGBC(c, 2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
                          GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0);
    pButtons.add(bCancel, c);

    // putting all together
    JPanel pAll = new JPanel(new GridBagLayout());
    c = ViewerUtil.setGBC(c, 0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.FIRST_LINE_START,
                          GridBagConstraints.BOTH, new Insets(8, 8, 0, 8), 0, 0);
    pAll.add(pGeneral, c);
    c = ViewerUtil.setGBC(c, 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.FIRST_LINE_START,
                          GridBagConstraints.BOTH, new Insets(8, 8, 4, 0), 0, 0);
    pAll.add(pBamV1, c);
    c = ViewerUtil.setGBC(c, 1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.FIRST_LINE_START,
                          GridBagConstraints.BOTH, new Insets(8, 4, 4, 8), 0, 0);
    pAll.add(pBamV2, c);
    c = ViewerUtil.setGBC(c, 0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                          GridBagConstraints.HORIZONTAL, new Insets(8, 8, 8, 8), 0, 0);
    pAll.add(pButtons, c);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(pAll, BorderLayout.CENTER);
    pack();
    setResizable(false);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), getRootPane());
    getRootPane().getActionMap().put(getRootPane(), new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent event) { setVisible(false); }
    });
    setLocationRelativeTo(getOwner());
    setVisible(true);
  }

  // Applies the default settings to the dialog controls
  private void setDefaults()
  {
    cbBamVersion.setSelectedIndex(DEFAULT_BAM_VERSION);
    tfPath.setText(DEFAULT_PATH);
    cbAutoClear.setSelected(DEFAULT_AUTO_CLEAR);
    cbCloseOnExit.setSelected(DEFAULT_CLOSE_ON_EXIT);
    sTransparency.setValue(Integer.valueOf(DEFAULT_TRANSPARENCY_THRESHOLD));
    cbCompressBam.setSelected(DEFAULT_COMPRESS_BAM);
    cbCompressionType.setSelectedIndex(DEFAULT_COMPRESSION_TYPE);
    sPvrzIndex.setValue(Integer.valueOf(DEFAULT_PVRZ_INDEX));
  }

  // Fetches the values from the dialog controls
  private void updateSettings()
  {
    bamVersion = cbBamVersion.getSelectedIndex();
    path = tfPath.getText();
    autoClear = cbAutoClear.isSelected();
    closeOnExit = cbCloseOnExit.isSelected();
    transparencyThreshold = (Integer)sTransparency.getValue();
    compressBam = cbCompressBam.isSelected();
    compressionType = cbCompressionType.getSelectedIndex();
    pvrzIndex = (Integer)sPvrzIndex.getValue();
    validateSettings();
  }
}