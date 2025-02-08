package Unknown;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ContactApp {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultListModel<String> contactListModel;
    private JList<String> contactList;
    private ArrayList<Contact> contacts;
    private JLabel nameLabel;
    private JLabel phoneLabel;
    private JLabel emailLabel;
    private JLabel profilePicture;
   private JTextField nameField;
   private JTextField phoneField;
   private JTextField emailField;
   private JTextField searchField;
   private JComboBox<String> groupSelector;
    private JCheckBox favoriteCheckBox;
    private String selectedProfileImage = null;
    public ContactApp() {
        frame = new JFrame("Contact");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout());
        GridLayout GL = new GridLayout(1,2);
        frame.setLayout(GL);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        contacts = new ArrayList<>();

        initContactListView();
        initContactDetailsView();
        initContactCreationView();

        frame.add(mainPanel);
        frame.setVisible(true);
    }
    public void initContactListView() {
        JPanel panel = new JPanel(new BorderLayout());
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchContacts());

        JButton addButton = new JButton("Add New Contact");
        JButton viewButton = new JButton("View Details");
        JButton deleteButton = new JButton("Delete Contact");

        addButton.addActionListener(e -> cardLayout.show(mainPanel, "CREATE"));
        viewButton.addActionListener(e -> showContactDetails());
        deleteButton.addActionListener(e -> deleteContact());

        JPanel topPanel = new JPanel();
        topPanel.add(searchField);
        topPanel.add(searchButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(contactList), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "LIST");
    }
    public void initContactDetailsView() {
        JPanel panel = new JPanel(new GridLayout(6, 1));
        nameLabel = new JLabel();
        phoneLabel = new JLabel();
        emailLabel = new JLabel();
        profilePicture = new JLabel("No Image", SwingConstants.CENTER);
        favoriteCheckBox = new JCheckBox("Mark as Favorite");

        JButton callButton = new JButton("Call");
        JButton messageButton = new JButton("Message");
        JButton editButton = new JButton("Edit");
        JButton backButton = new JButton("Back to List");

        callButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Calling..."));
        messageButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Messaging..."));
        editButton.addActionListener(e -> editContact());
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(callButton);
        buttonPanel.add(messageButton);
        buttonPanel.add(editButton);
        buttonPanel.add(backButton);

        panel.add(profilePicture);
        panel.add(nameLabel);
        panel.add(phoneLabel);
        panel.add(emailLabel);
        panel.add(favoriteCheckBox);
        panel.add(buttonPanel);

        mainPanel.add(panel, "DETAILS");
    }
    public void initContactCreationView() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(10);
        phoneField = new JTextField(10);
        emailField = new JTextField(10);
        groupSelector = new JComboBox<>(new String[]{"Family", "Friends", "Work"});

        JButton selectImageButton = new JButton("Select");
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        Dimension buttonSize = new Dimension(80, 25);
        JButton[] buttons = {selectImageButton, saveButton, cancelButton};
        for (JButton button : buttons) {
            button.setPreferredSize(buttonSize);
        }

        selectImageButton.addActionListener(e -> chooseProfileImage());
        saveButton.addActionListener(e -> saveContact());
        cancelButton.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));

        JLabel[] labels = {new JLabel("Name:"), new JLabel("Phone:"), new JLabel("Email:"), new JLabel("Group:")};
        Component[] fields = {nameField, phoneField, emailField, groupSelector};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(labels[i], gbc);

            gbc.gridx = 1;
            panel.add(fields[i], gbc);
        }

        for (int i = 0; i < buttons.length; i++) {
            gbc.gridx = i;
            gbc.gridy = labels.length;
            panel.add(buttons[i], gbc);
        }

        mainPanel.add(panel, "CREATE");
    }
    public JPanel createInputPanel(int width, int height) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)); // Compact layout
        panel.setPreferredSize(new Dimension(width, height));
        return panel;
    }
    public void showContactDetails() {
        int index = contactList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a contact.");
            return;
        }

        Contact contact = contacts.get(index);
        nameLabel.setText("Name: " + contact.name);
        phoneLabel.setText("Phone: " + contact.phone);
        emailLabel.setText("Email: " + contact.email);
        favoriteCheckBox.setSelected(contact.isFavorite);

        if (contact.imagePath != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(contact.imagePath).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            profilePicture.setIcon(icon);
        } else {
            profilePicture.setText("No Image");
        }

        cardLayout.show(mainPanel, "DETAILS");
    }

    public void saveContact() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String group = (String) groupSelector.getSelectedItem();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields must be filled!");
            return;
        }

        contacts.add(new Contact(name, phone, email, group, selectedProfileImage, false));
        contactListModel.addElement(name);

        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        selectedProfileImage = null;

        cardLayout.show(mainPanel, "LIST");
    }

    public void editContact() {
        int index = contactList.getSelectedIndex();
        if (index == -1) return;

        Contact contact = contacts.get(index);
        nameField.setText(contact.name);
        phoneField.setText(contact.phone);
        emailField.setText(contact.email);
        selectedProfileImage = contact.imagePath;

        contacts.remove(index);
        contactListModel.remove(index);

        cardLayout.show(mainPanel, "CREATE");
    }
    public void deleteContact() {
        int index = contactList.getSelectedIndex();
        if (index != -1) {
            contacts.remove(index);
            contactListModel.remove(index);
        }
    }
    public void searchContacts() {
        String query = searchField.getText().toLowerCase();
        DefaultListModel<String> filteredModel = new DefaultListModel<>();

        for (Contact contact : contacts.stream().filter(c -> c.name.toLowerCase().contains(query)).collect(Collectors.toList())) {
            filteredModel.addElement(contact.name);
        }

        contactList.setModel(filteredModel);
    }

    public void clearSearch() {
        searchField.setText("");
        contactList.setModel(contactListModel);
    }

    public void chooseProfileImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));

        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            selectedProfileImage = fileChooser.getSelectedFile().getAbsolutePath();

            ImageIcon icon = new ImageIcon(new ImageIcon(selectedProfileImage)
                    .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            profilePicture.setIcon(icon);
            profilePicture.setText("");
        }
    }
    public static class Contact {
        String name, phone, email, group, imagePath;
        boolean isFavorite;

        Contact(String name, String phone, String email, String group, String imagePath, boolean isFavorite) {
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.group = group;
            this.imagePath = imagePath;
            this.isFavorite = isFavorite;
        }
    }
}

