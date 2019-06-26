package javaapplication1;




import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.text.*;
import javax.swing.text.DefaultEditorKit.*;
import javax.swing.text.DefaultEditorKit.CopyAction;
import javax.swing.text.DefaultEditorKit.CutAction;
import javax.swing.text.DefaultEditorKit.PasteAction;
import javax.swing.text.StyledEditorKit.*;
import javax.swing.text.StyledEditorKit.AlignmentAction;
import javax.swing.text.StyledEditorKit.BoldAction;
import javax.swing.text.StyledEditorKit.FontFamilyAction;
import javax.swing.text.StyledEditorKit.FontSizeAction;
import javax.swing.text.StyledEditorKit.ItalicAction;
import javax.swing.text.StyledEditorKit.UnderlineAction;
import javax.swing.undo.*;

public class MyEditor3 {

    private JFrame frame__; //This is the main frame window to be displayed
   // private JFrame shapes_frame;
    private JTextPane editor__; //the text editor main pane is this one
    private JComboBox<String> fontSizeComboBox__; //The drop down for available font sizes
    private JComboBox<String> textAlignComboBox__;  //the drop down for the available text align options
    private JComboBox<String> fontFamilyComboBox__; //the drop down for the available fonts styles
    private UndoManager undoMgr__; //Manages the undo operation

    enum UndoActionType {

        UNDO, REDO
    };

    private static final String MAIN_TITLE = "My Editor 3";
    private static final String DEFAULT_FONT_FAMILY = "SansSerif";
    private static final int DEFAULT_FONT_SIZE = 18;
    private static final List<String> FONT_LIST = Arrays.asList(new String[]{"Arial", "Calibri", "Cambria", "Courier New", "Comic Sans MS", "Dialog", "Georgia", "Helevetica", "Lucida Sans", "Monospaced", "Tahoma", "Times New Roman", "Verdana"});
    private static final String[] FONT_SIZES = {"Font Size", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30"};
    private static final String[] TEXT_ALIGNMENTS = {"Text Align", "Left", "Center", "Right", "Justified"};

    JFileChooser dialog = new JFileChooser(System.getProperty("user.dir")); 
    String currentFile = "Untitled"; //initial text named untitled
    boolean changed = false;

    public static void main(String[] args)
            throws Exception {

        UIManager.put("TextPane.font",
                new Font(DEFAULT_FONT_FAMILY, Font.PLAIN, DEFAULT_FONT_SIZE)); //setting default values for the text style and size
        UIManager.setLookAndFeel(new NimbusLookAndFeel()); //modifying the look and feel of the document

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                new MyEditor3().createAndShowGUI(); //creating an object
            }
        });
    }

    private void createAndShowGUI() {

        frame__ = new JFrame(MAIN_TITLE); //setting up the frame
        editor__ = new JTextPane(); //new pane
        JScrollPane editorScrollPane = new JScrollPane(editor__); //allowing the pane to scroll if text reaches end of frame size

        editor__.setDocument(getNewDocument()); //new document generated

        undoMgr__ = new UndoManager();
        
        //adding edit capacity to cut, copy, underline, bold, italisize and paste
        EditButtonActionListener editButtonActionListener = new EditButtonActionListener(); 

        JButton cutButton = new JButton(new CutAction()); //allows text on editor to be cut
        cutButton.setHideActionText(true);
        cutButton.setText("Cut");
        cutButton.addActionListener(editButtonActionListener); 
        JButton copyButton = new JButton(new CopyAction()); //allows text on editor to be copied
        copyButton.setHideActionText(true);
        copyButton.setText("Copy");
        copyButton.addActionListener(editButtonActionListener);
        JButton pasteButton = new JButton(new PasteAction()); //allows text on editor to be pasted
        pasteButton.setHideActionText(true);
        pasteButton.setText("Paste");
        pasteButton.addActionListener(editButtonActionListener);

        JButton boldButton = new JButton(new BoldAction()); //implements bold action on text
        boldButton.setHideActionText(true);
        boldButton.setText("Bold");
        boldButton.addActionListener(editButtonActionListener);
        JButton italicButton = new JButton(new ItalicAction()); //italicizes selected text
        italicButton.setHideActionText(true);
        italicButton.setText("Italic");
        italicButton.addActionListener(editButtonActionListener);
        JButton underlineButton = new JButton(new UnderlineAction()); //underlines selected text
        underlineButton.setHideActionText(true);
        underlineButton.setText("Underline");
        underlineButton.addActionListener(editButtonActionListener);

        String[] cases = {"Case", "UPPERCASE", "lowercase", "Toggle cASE"}; //defines available cases on text 

        JComboBox caseList = new JComboBox(cases);
        ActionListener caseAction = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) { //action performed when the cases are selected
                try {
                    int begin = editor__.getSelectionStart();
                    int end = editor__.getSelectionEnd();
                    String selectedText = editor__.getText().substring(begin, end);
                    String s = (String) caseList.getSelectedItem();
                    caseList.setSelectedIndex(0);
                    editor__.requestFocusInWindow();
                    switch (s) {
                        case "UPPERCASE":
                            String temp = editor__.getText().substring(0,begin) + selectedText.toUpperCase() + editor__.getText().substring(begin+selectedText.length());
                            editor__.setText(temp);
                            break;
                        case "lowercase":
                            temp = editor__.getText().substring(0,begin) + selectedText.toLowerCase() + editor__.getText().substring(begin+selectedText.length());
                            editor__.setText(temp);
                            break;
                        case "Toggle cASE":
                            temp = editor__.getText().substring(0,begin) + toggle(selectedText) + editor__.getText().substring(begin+selectedText.length());
                            editor__.setText(temp);
                            break;
                    }
                } catch (Exception e34) {
                    e34 = new Exception();
                    return;
                }

            }

            private String toggle(String selectedText) { //toggles case
                String s = "";
                for (int i = 0; i < selectedText.length(); i++) {
                    if (Character.isUpperCase(selectedText.charAt(i))) {
                        s = s + Character.toLowerCase(selectedText.charAt(i));
                    } else if (Character.isLowerCase(selectedText.charAt(i))) {
                        s = s + Character.toUpperCase(selectedText.charAt(i));
                    } else {
                        s = s + selectedText.charAt(i);
                    }
                }
                return s;
            }

        };
        caseList.addActionListener(caseAction); //adds action to case drop down menu

        JTextField replace = new JTextField("Old text"); //text field that accepts text to be changed
        JTextField replaceWith = new JTextField("New text"); //text feild that accpts new text

        JButton Replace = new JButton("Replace");
        ActionListener Replace1 = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String oldWord = replace.getText();
                    String newWord = replaceWith.getText();
                    if (!oldWord.equals("")) { 
                        String editorText = editor__.getSelectedText();
                        editorText = editorText.replaceAll(oldWord, newWord); //inbuilt function to replace all words on selected text
                        editor__.setText(editor__.getText().substring(0, editor__.getSelectionStart())+editorText+ editor__.getText().substring(editor__.getSelectionEnd())); //putting the new text back
                    }
                } catch (Exception e88) {
                    e88 = new Exception();
                    return;
                }
            }
        };
        Replace.addActionListener(Replace1);

        JButton ReplaceAll = new JButton("Replace All"); //replaces all occurances of old with new text
        ActionListener ReplaceA = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String oldWord = replace.getText();
                    String newWord = replaceWith.getText();
                    if (!oldWord.equals("")) {
                        String editorText = editor__.getText();
                        editorText = editorText.replaceAll(oldWord, newWord); //replaces the whole document's old words 
                        editor__.setText(editorText);
                    }
                } catch (Exception e88) {
                    e88 = new Exception();
                    return;
                }
            }
        };
        ReplaceAll.addActionListener(ReplaceA);

        JButton find = new JButton("Find"); //finds a particular text in the document
        JTextField searchField = new JTextField("Find Word");
        ActionListener findWord = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String editorText = editor__.getText();

                // Get the string the user wants to search for
                String searchValue = searchField.getText();

                editorText = editorText.toLowerCase();
                searchValue = searchValue.toLowerCase();
 
                // Find the next occurrence, searching forward or backward depending on the setting of the reverse box.
                int start;
                try {

                    start = editorText.lastIndexOf(searchValue, editor__.getSelectionStart() - 1);

                    // If the string was found, move the selection so that the found string is highlighted.
                    // String methods is much more important.
                    if (start != -1) {
                        editor__.setCaretPosition(start);
                        editor__.moveCaretPosition(start + searchValue.length());
                        editor__.getCaret().setSelectionVisible(true); 
                    }
                    else
                    {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(frame__, "Editor can't find the word " );
                    }
                } catch (Exception e22) {
                    e22 = new Exception();
                    
                }
            }
        };
        find.addActionListener(findWord);

        textAlignComboBox__ = new JComboBox<String>(TEXT_ALIGNMENTS); 
        textAlignComboBox__.setEditable(false);
        textAlignComboBox__.addItemListener(new TextAlignItemListener()); //adds functionality to alignments available

        fontSizeComboBox__ = new JComboBox<String>(FONT_SIZES);
        fontSizeComboBox__.setEditable(false); //the combobox is not editable, order is not alterable
        fontSizeComboBox__.addItemListener(new FontSizeItemListener());

        Vector<String> editorFonts = getEditorFonts(); //gets a vector of the available fonts 
        editorFonts.add(0, "Font Family"); 
        fontFamilyComboBox__ = new JComboBox<String>(editorFonts); //adds these fonts to the drop down of font family
        fontFamilyComboBox__.setEditable(false);
        fontFamilyComboBox__.addItemListener(new FontFamilyItemListener());

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(new UndoActionListener(UndoActionType.UNDO));//implements undo action
        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(new UndoActionListener(UndoActionType.REDO)); //implements redo action

        JButton countWords = new JButton("Count Words"); //button that counts the number of words in the document 
        JTextField word = new JTextField(" Word Count "); //displays the word count
        JButton countCharacters = new JButton("Count Characters"); //counts the characters on pressing 
        JTextField character = new JTextField(" Character Count "); //displays the characters present in the text area
        ActionListener wordCount = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String selectedText = editor__.getSelectedText();
                    editor__.requestFocusInWindow();
                    int wordcount = 1;
                    for (int i = 0; i < selectedText.length(); i++) { //funtion to count the number of words
                        if (selectedText.charAt(i) == ' ') {
                            wordcount++;
                        }
                    }
                    String temp = "" + wordcount;
                    word.setText(temp);
                } catch (Exception e34) {
                    e34 = new Exception();
                    return;
                }
            }

        };
        countWords.addActionListener(wordCount);

        ActionListener charCount = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String selectedText = editor__.getSelectedText();
                    editor__.requestFocusInWindow();
                    int charcount = 0;
                    for (int i = 0; i < selectedText.length(); i++) {
                        if (Character.isLetterOrDigit(selectedText.charAt(i))) { //funtion to count the number of characters
                            charcount++;
                        }
                    }
                    character.setText("" + charcount);
                } catch (Exception e34) {
                    e34 = new Exception();
                    return;
                }
            }
        };
        countCharacters.addActionListener(charCount);

        //buttons to open, save and saveAs the document
        JButton open = new JButton("Open"); 
        JButton save = new JButton("Save");
        JButton saveAs = new JButton("Save As");

        ActionListener file = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (ae.getSource() == open) { //function to implement the opening of an exisiting document
                    saveOld();
                    if (dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        readInFile(dialog.getSelectedFile().getAbsolutePath());
                    }
                } else if (ae.getSource() == save) {  //function to implement the saving of the created document
                    if (!currentFile.equals("Untitled")) {
                        saveFile(currentFile);
                    } else {
                        saveFileAs();
                    }
                } else if (ae.getSource() == saveAs) { //function to implement the changing of extention of exisiting document
                    saveFileAs();
                }
            }

            public void saveFileAs() {
                if (dialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    saveFile(dialog.getSelectedFile().getAbsolutePath());
                }

            }

            public void saveOld() {
                if (changed) {
                    if (JOptionPane.showConfirmDialog(frame__, "Would you like to save " + currentFile + " ?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        saveFile(currentFile);
                    }
                }
            }

            void readInFile(String fileName) {
                try {
                    FileReader r = new FileReader(fileName);
                    editor__.read(r, null);
                    r.close();
                    currentFile = fileName;
                    frame__.setTitle(currentFile);
                    changed = false;
                } catch (IOException e) {
                    e = new IOException();
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(frame__, "Editor can't find the file called " + fileName);
                }
            }

            void saveFile(String fileName) {
                try {
                    FileWriter w = new FileWriter(fileName);
                    editor__.write(w);
                    w.close();
                    currentFile = fileName;
                    frame__.setTitle(currentFile);
                    changed = false;

                } catch (IOException e) {
                }
            }
        };

        open.addActionListener(file);
        save.addActionListener(file);
        saveAs.addActionListener(file);

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT)); //panel1 that has all the available drop down menus

        JComboBox fileMenu  //adds the buttons to the file drop down menu
                = new JComboBox(new Object[]{
                    "File",
                    open,
                    save,
                    saveAs
                }
                );

        panel1.add(fileMenu);
        boldButton.setBounds(fileMenu.getBounds());
        fileMenu.setRenderer(new javaapplication1.MyEditor3.ButtonComboBoxRenderer()); //renders the functionality of the buttons in this combobox
        fileMenu.addActionListener(new javaapplication1.MyEditor3.ButtonComboBoxListener(frame__, fileMenu));
        panel1.add(new JSeparator(SwingConstants.VERTICAL)); //separator to separate previous button and next one
        JComboBox editMenu //edit menu carries all the edit options, works the same way as file menu 
                = new JComboBox(new Object[]{
                    "Edit",
                    cutButton,
                    copyButton,
                    pasteButton,
                    undoButton,
                    redoButton
                }
                );
        panel1.add(editMenu);
        editMenu.setRenderer(new javaapplication1.MyEditor3.ButtonComboBoxRenderer());
        editMenu.addActionListener(new javaapplication1.MyEditor3.ButtonComboBoxListener(frame__, editMenu));

        panel1.add(new JSeparator(SwingConstants.VERTICAL));
        JComboBox toolMenu //tool menu has the tools available, works as file and edit menus
                = new JComboBox(new Object[]{
                    "Tools",
                    boldButton,
                    italicButton,
                    underlineButton
                }
                );
        panel1.add(toolMenu);
        toolMenu.setRenderer(new javaapplication1.MyEditor3.ButtonComboBoxRenderer());
        toolMenu.addActionListener(new javaapplication1.MyEditor3.ButtonComboBoxListener(frame__, toolMenu));
        

        JButton shape = new JButton("Click here for Shapes->"); //button that opens a JFrame for shapes
        
        ActionListener shapes = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Shapes s = new Shapes();
            }
        };
        shape.addActionListener(shapes); 
        
        
        //the following lines show the addition of all the combo boxes and buttons placed in the first pane
        panel1.add(new JSeparator(SwingConstants.VERTICAL));
        panel1.add(new JSeparator(SwingConstants.VERTICAL));
        panel1.add(fontSizeComboBox__);
        panel1.add(new JSeparator(SwingConstants.VERTICAL));
        panel1.add(fontFamilyComboBox__); 
        panel1.add(new JSeparator(SwingConstants.VERTICAL));
        panel1.add(caseList);
        panel1.add(new JSeparator(SwingConstants.VERTICAL));
        panel1.add(textAlignComboBox__);
        panel1.add(shape);
        

        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT)); //another panel created
        
        //the following lines show the addition of all the buttons placed in the second pane 
        panel2.add(searchField);
        panel2.add(find);
        panel2.add(new JSeparator(SwingConstants.VERTICAL));
        panel2.add(replace);
        panel2.add(replaceWith);
        panel2.add(Replace);
        panel2.add(ReplaceAll);
        panel2.add(new JSeparator(SwingConstants.VERTICAL));
        panel2.add(countWords);
        panel2.add(word);
        panel2.add(countCharacters);
        panel2.add(character);
        
        
      
        //toolbar panel created to neatly add the two panes along the page axis
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BoxLayout(toolBarPanel, BoxLayout.PAGE_AXIS)); //boxlayout employed for better display capacity
        toolBarPanel.add(panel1);
        toolBarPanel.add(panel2);
       
        frame__.add(toolBarPanel, BorderLayout.NORTH); //frame places the tool bar on the north
        editorScrollPane.setSize(1500, 600);
        
        frame__.add(editorScrollPane, BorderLayout.CENTER); //editor pane placed on the center of frame
        
        //following lines show the technicalities of the displayed frame including its size and location
        frame__.setSize(1000, 500);
        frame__.setLocation(110, 80);
        frame__.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame__.setVisible(true);

        editor__.requestFocusInWindow();

    }

    class ButtonComboBoxRenderer extends BasicComboBoxRenderer implements ListCellRenderer { //class that renders combobox of buttons

        public ButtonComboBoxRenderer() {
            super(); //call made to parent class in the constructor
        }

        public Component getListCellRendererComponent(JList list,
                Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {           //higlights selected combobox option 
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setFont(list.getFont());
            if (value instanceof Icon) { 
                setIcon((Icon) value);
            }
            if (value instanceof JButton) {
                return (Component) value; //button functionlity returned if the button is pressed
            } else {
                setText((value == null) ? "" : value.toString()); //text is set to the string value of the option chosen
            }

            return this;
        }
    }

    class ButtonComboBoxListener implements ActionListener {

        JComboBox combobox;
        JFrame frame;

        ButtonComboBoxListener(JFrame frame, JComboBox combobox) {
            this.frame = frame;
            this.combobox = combobox;
            combobox.setSelectedIndex(0); 
        }

        public void actionPerformed(ActionEvent e) {
            Object selectedItem = combobox.getSelectedItem();
            if (selectedItem instanceof JButton) {
                ((JButton) selectedItem).doClick(); //perforns the click action on the chosen button in the combo box
            }
        }
    }

    private StyledDocument getNewDocument() { //creation of a new document 

        StyledDocument doc = new DefaultStyledDocument();
        doc.addUndoableEditListener(new UndoEditListener()); 
        return doc;
    }

    private Vector<String> getEditorFonts() {

        String[] availableFonts
                = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(); //availableFonts stores all acceptable fonts
        Vector<String> returnList = new Vector<>();

        for (String font : availableFonts) {

            if (FONT_LIST.contains(font)) {

                returnList.add(font); //the chosen font is implemented
            }
        }

        return returnList;
    }

    private class EditButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            editor__.requestFocusInWindow(); //highlights the text on window
        }
    }

    private class TextAlignItemListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {

            if ((e.getStateChange() != ItemEvent.SELECTED)
                    || (textAlignComboBox__.getSelectedIndex() == 0)) {

                return;
            }

            String alignmentStr = (String) e.getItem();
            int newAlignment = textAlignComboBox__.getSelectedIndex() - 1; //choses alignment on basis of the element value on combobox 
            
            // New alignment is set based on these values defined in StyleConstants:
            // ALIGN_LEFT 0, ALIGN_CENTER 1, ALIGN_RIGHT 2, ALIGN_JUSTIFIED 3
            textAlignComboBox__.setAction(new AlignmentAction(alignmentStr, newAlignment));
            textAlignComboBox__.setSelectedIndex(0); // initialize to (default) select
            editor__.requestFocusInWindow();
        }
    } // TextAlignItemListener

    private class FontSizeItemListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {

            if ((e.getStateChange() != ItemEvent.SELECTED)
                    || (fontSizeComboBox__.getSelectedIndex() == 0)) {

                return;
            }

            String fontSizeStr = (String) e.getItem();
            int newFontSize = 0;

            try {
                newFontSize = Integer.parseInt(fontSizeStr); //the selected string is altered to the new font size
            } catch (NumberFormatException ex) {

                return;
            }

            fontSizeComboBox__.setAction(new FontSizeAction(fontSizeStr, newFontSize));
            fontSizeComboBox__.setSelectedIndex(0); // initialize to (default) select
            editor__.requestFocusInWindow();
        }
    } // FontSizeItemListener

    private class FontFamilyItemListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {

            if ((e.getStateChange() != ItemEvent.SELECTED)
                    || (fontFamilyComboBox__.getSelectedIndex() == 0)) {

                return;
            }

            String fontFamily = (String) e.getItem();
            fontFamilyComboBox__.setAction(new FontFamilyAction(fontFamily, fontFamily));
            fontFamilyComboBox__.setSelectedIndex(0); // initialize to (default) select
            editor__.requestFocusInWindow();
        }
    } // FontFamilyItemListener

    private class UndoEditListener implements UndoableEditListener {

        @Override
        public void undoableEditHappened(UndoableEditEvent e) {

            undoMgr__.addEdit(e.getEdit()); // remember the edit
        }
    }

    private class UndoActionListener implements ActionListener {

        private UndoActionType undoActionType;

        public UndoActionListener(UndoActionType type) {

            undoActionType = type;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            switch (undoActionType) {

                case UNDO:
                    if (!undoMgr__.canUndo()) {

                        editor__.requestFocusInWindow();
                        return; // no edits to undo
                    }

                    undoMgr__.undo(); //performs undo
                    break;

                case REDO:
                    if (!undoMgr__.canRedo()) { //checks if its possible undo or redo

                        editor__.requestFocusInWindow();
                        return; // no edits to redo
                    }

                    undoMgr__.redo(); 
            }

            editor__.requestFocusInWindow();
        }
    } // UndoActionListener

    private StyledDocument getEditorDocument() {

        StyledDocument doc = (DefaultStyledDocument) editor__.getDocument();
        return doc;
    }
    
}

