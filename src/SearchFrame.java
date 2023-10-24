import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class SearchFrame extends JFrame {

    public static void main(String[] args) {
        getFonts();
        getImages();
        SearchFrame searchFrame = new SearchFrame();
        searchFrame.setVisible(true);
    }

    public final int WIDTH, HEIGHT; // width and height of frame
    static Font TINY_PLAIN, TINY_ITALICS, SMALL_PLAIN, SMALL_BOLD, MED_PLAIN, MED_BOLD, BIG_PLAIN, BIG_BOLD;
    static Image SEARCH_IMAGE;
    static ImageIcon SEARCH_ICON, SEARCH_ICON_DISABLED;
    final String SEARCH_PANEL = "search",
            RESULTS_PANEL = "results";
    SearchManager searchManager;
    SearchStream searchStream;
    JPanel cardPnl, searchPnl, resultsPnl;
    JLabel searchLbl;
    JTextField searchField;
    JButton loadFromDocuments, loadFromProjectFolder, searchBtn;
    JTextArea fullText, filterText, fileNameLbl;

    public SearchFrame() {
        super("Search");

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int screenWidth = screenSize.width,
                screenHeight = screenSize.height;
        WIDTH = screenWidth * 3 / 4;
        HEIGHT = screenHeight * 3 / 4;
        setSize(WIDTH, HEIGHT);
        setLocation((screenWidth - WIDTH) / 2, (screenHeight - HEIGHT) / 3);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                    UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        changeFrameBorder("Search Stream", BIG_BOLD, Color.BLACK,
                TitledBorder.CENTER, TitledBorder.TOP);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(SEARCH_IMAGE);

        searchManager = new SearchManager();

        cardPnl = new JPanel(new CardLayout());
        cardPnl.setOpaque(false);

        addSearchPanel();
        addResultsPanel();

        JPanel bottomPnl = new JPanel(new GridBagLayout());
        bottomPnl.setOpaque(false);
        bottomPnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton quitBtn = createButton("Quit", e -> {
            if (JOptionPane.showConfirmDialog(null, "Quit?", "Quit",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null)
                    == JOptionPane.OK_OPTION) {
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }
        });

        fileNameLbl = createTextArea();
        fileNameLbl.setFont(TINY_ITALICS);
        fileNameLbl.setForeground(Color.DARK_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bottomPnl.add(fileNameLbl, gbc);
        gbc.gridwidth = 1;
        gbc.ipadx = 0;
        gbc.fill = GridBagConstraints.NONE;
        bottomPnl.add(quitBtn, gbc);

        setLayout(new BorderLayout());
        add(cardPnl);
        add(bottomPnl, BorderLayout.SOUTH);
        ((CardLayout) cardPnl.getLayout()).show(cardPnl, SEARCH_PANEL);
        revalidate();
    }

    public void loadFileFromDocuments() {

        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Select file");
        jfc.setCurrentDirectory(new File(System.getProperty("user.home") + "//documents//"));
        jfc.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            searchManager.setFile(jfc.getSelectedFile());
        }
        searchLbl.setEnabled(true);
        searchField.setEnabled(true);
        searchField.requestFocusInWindow();
    }

    public void loadFileFromProject() {

        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Select file");
        jfc.setCurrentDirectory(new File(System.getProperty("user.dir") + "//sample//"));
        jfc.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            searchManager.setFile(jfc.getSelectedFile());
            searchLbl.setEnabled(true);
            searchField.setEnabled(true);
            searchField.requestFocusInWindow();
        }
    }

    private void addSearchPanel() {
        searchPnl = new JPanel();
        searchPnl.setOpaque(false);

        searchLbl = new JLabel(SEARCH_ICON);
        searchLbl.setOpaque(false);
        searchLbl.setHorizontalTextPosition(SwingConstants.RIGHT);
        searchLbl.setFont(SMALL_BOLD);
        searchLbl.setDisabledIcon(SEARCH_ICON_DISABLED);
        searchLbl.setEnabled(false);

        searchField = new JTextField();
        searchField.setOpaque(false);
        searchField.setFont(SMALL_PLAIN);
        searchField.setColumns(60);
        searchField.setEnabled(false);
        searchField.addActionListener(e -> {
            if (searchManager.updateQuery(searchField.getText())) {
                searchBtn.setEnabled(true);
                searchBtn.requestFocusInWindow();
            }
        });

        Box box = Box.createHorizontalBox();
        box.add(searchLbl);
        box.add(Box.createHorizontalStrut(5));
        box.add(searchField);

        GridBagLayout layout = new GridBagLayout();
        searchPnl.setLayout(layout);
        GridBagConstraints cons = new GridBagConstraints();

        JPanel invisible1 = new JPanel();
        invisible1.setOpaque(false);
        cons.gridheight = 3;
        cons.ipadx = WIDTH / 8;
        cons.insets = new Insets(20, 20, 20, 20);
        searchPnl.add(invisible1, cons);

        JPanel invisible2 = new JPanel();
        invisible2.setOpaque(false);
        cons.gridx = 6;
        searchPnl.add(invisible2, cons);

        cons.insets = new Insets(2, 2, 2, 2);
        cons.ipadx = 0;
        cons.gridheight = 1;
        cons.gridx = 1;
        cons.gridy = 1;
        cons.fill = GridBagConstraints.NONE;
        searchPnl.add(searchLbl, cons);

        cons.gridx = 2;
        cons.gridwidth = 4;
        cons.fill = GridBagConstraints.HORIZONTAL;
        searchPnl.add(searchField, cons);

        JPanel btnPnl = new JPanel();
        btnPnl.setOpaque(false);
        loadFromDocuments = createButton("Load file from 'documents'",
                e -> loadFileFromDocuments());
        loadFromProjectFolder = createButton("Load file from project directory",
                e -> loadFileFromProject());

        searchBtn = createButton("Search", e -> {
            // the search button's ActionListener

            searchStream = new SearchStream(searchManager);

            List<String> fileText = searchStream.readFile();
            StringBuilder full = new StringBuilder();
            for (String s : fileText) {
                full.append(s).append('\n');
            }
            if (full.length() > 1) {
                full.delete(full.length() - 2, full.length());
            }
            fullText.setText(full.toString());
            fullText.select(0, 0);

            List<String> searchMatches = searchStream.search();
            StringBuilder filtered = new StringBuilder();
            for (String s : searchMatches) {
                filtered.append(s).append('\n');
            }
            if (filtered.length() > 1) {
                filtered.delete(filtered.length() - 2, filtered.length());
            }
            filterText.setText(filtered.toString());
            filterText.select(0, 0);

            changeFrameBorder("Results for \"" + searchManager.getSearch() + "\"",TINY_ITALICS,
                    Color.DARK_GRAY, TitledBorder.LEFT, TitledBorder.BELOW_TOP);

            fileNameLbl.setText(searchManager.getFile().toString());
            ((CardLayout) cardPnl.getLayout()).show(cardPnl, RESULTS_PANEL);
            revalidate();
        });

        searchBtn.setEnabled(false);
        searchBtn.setMnemonic(KeyEvent.VK_ENTER);

        cons.gridx = 2;
        cons.gridy = 3;
        cons.gridwidth = 2;
        cons.gridheight = 1;
        cons.fill = GridBagConstraints.NONE;
        cons.anchor = GridBagConstraints.NORTHWEST;
        searchPnl.add(loadFromDocuments, cons);
        cons.gridy = 4;
        searchPnl.add(loadFromProjectFolder, cons);

        cons.gridx = 4;
        cons.gridy = 3;
        cons.gridwidth = 1;
        cons.gridheight = 2;
        cons.ipadx = WIDTH / 12;
        searchPnl.add(new JPanel(), cons);

        cons.anchor = GridBagConstraints.NORTHEAST;
        cons.gridx = 5;
        cons.gridy = 3;
        cons.gridwidth = 1;
        cons.gridheight = 1;
        cons.ipadx = 0;
        searchPnl.add(searchBtn, cons);

        cardPnl.add(searchPnl, SEARCH_PANEL);
    }

    private void addResultsPanel() {
        resultsPnl = new JPanel();
        resultsPnl.setOpaque(false);
        resultsPnl.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        fullText = createTextArea();
        filterText = createTextArea();

        JScrollPane fullScroll = createScrollPane(fullText),
                filterScroll = createScrollPane(filterText);

        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK, 2, true);
        TitledBorder b1 = BorderFactory.createTitledBorder(lineBorder, "Original Text",
                        TitledBorder.CENTER, TitledBorder.ABOVE_TOP),
                b2 = BorderFactory.createTitledBorder(lineBorder, "Search Results",
                        TitledBorder.CENTER, TitledBorder.ABOVE_TOP);
        b1.setTitleFont(MED_BOLD);
        b2.setTitleFont(MED_BOLD);
        fullScroll.setBorder(b1);
        filterScroll.setBorder(b2);

        resultsPnl.setLayout(new BoxLayout(resultsPnl, BoxLayout.Y_AXIS));
        Box xBox = Box.createHorizontalBox();
        xBox.add(fullScroll);
        xBox.add(Box.createHorizontalStrut(10));
        xBox.add(filterScroll);
        resultsPnl.add(xBox);

        cardPnl.add(resultsPnl, RESULTS_PANEL);
    }

    private static JButton createButton(String text, ActionListener actionListener) {
        JButton btn = new JButton(text);
        btn.addActionListener(actionListener);
        btn.setFocusPainted(false);
        btn.setFont(SMALL_PLAIN);
        return btn;
    }

    private static JTextArea createTextArea() {
        JTextArea ta = new JTextArea();
        ta.setOpaque(false);
        ta.setFont(SMALL_PLAIN);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        return ta;
    }

    private static JScrollPane createScrollPane(Component component) {
        JScrollPane sp = new JScrollPane(component);
        sp.setOpaque(false);
        sp.setBorder(null);
        sp.getViewport().setBorder(null);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private void changeFrameBorder(String title, Font font, Color color, int justification, int position) {
        TitledBorder frameBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder
                        (Color.BLACK, 3, true), title,
                justification, position);
        frameBorder.setTitleColor(color);
        frameBorder.setTitleFont(font);
        getRootPane().setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder
                (20, 20, 20, 20), frameBorder));
    }

    private static void getFonts() {
        String dir = System.getProperty("user.dir") + "//fonts//";
        try {
            Font reg = Font.createFont(Font.TRUETYPE_FONT, new File(dir +
                    "//Hack-Regular.ttf"));
            Font bold = Font.createFont(Font.TRUETYPE_FONT, new File(dir +
                    "//Hack-Bold.ttf"));
            Font italics = Font.createFont(Font.TRUETYPE_FONT, new File(dir +
                    "//Hack-Italic.ttf"));
            TINY_PLAIN = reg.deriveFont(Font.PLAIN, 16f);
            TINY_ITALICS = italics.deriveFont(Font.PLAIN, 16f);
            SMALL_PLAIN = reg.deriveFont(Font.PLAIN, 18f);
            SMALL_BOLD = bold.deriveFont(Font.PLAIN, 18f);
            MED_PLAIN = reg.deriveFont(Font.PLAIN, 24f);
            MED_BOLD = bold.deriveFont(Font.PLAIN, 24f);
            BIG_PLAIN = reg.deriveFont(Font.PLAIN, 28f);
            BIG_BOLD = bold.deriveFont(Font.PLAIN, 28f);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getImages() {
        String dir = System.getProperty("user.dir") + "//images//";
        try {
            SEARCH_IMAGE = ImageIO.read(new File(dir + "//search.png"));
            SEARCH_ICON = getSmoothIcon(dir + "//search.png");
            SEARCH_ICON_DISABLED = getSmoothIcon(dir + "//search_disabled.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ImageIcon getSmoothIcon(String fileName) {
        return new ImageIcon(Toolkit.getDefaultToolkit().getImage(fileName)) {
            @Override
            public int getIconWidth() {
                return 20;
            }

            @Override
            public int getIconHeight() {
                return 20;
            }

            @Override
            public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
                g.drawImage(getImage(), x, y, 20, 20, null);
            }
        };
    }
}