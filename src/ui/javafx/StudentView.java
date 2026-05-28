package ui.javafx;

import exceptions.StudentNotFoundException;
import model.MonsterType;
import model.Student;
import service.StudentService;
import service.MonsterTypeService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

/**
 * View for Student Management in the Monster High Institute Manager.
 *
 * <p>Displays all students in a {@link TableView} and allows the user to:
 * <ul>
 *   <li>Search students by name or ID</li>
 *   <li>Add a new student via a modal dialog (also callable from the Dashboard)</li>
 *   <li>Edit an existing student via a pre-filled modal dialog</li>
 *   <li>Delete a student after confirmation</li>
 * </ul>
 * @author Fatima Roman
 * @version 3.1
 */
public class StudentView {

    /** The main JavaFX window. */
    private final Stage stage;

    /** Reference to the dashboard, used to navigate back and reuse nav buttons. */
    private final DashboardView dashboard;

    /** Service layer for student persistence operations. */
    private final StudentService studentService = new StudentService();

    /** Service layer for monster-type lookup (used to populate the combo box). */
    private final MonsterTypeService monsterTypeService = new MonsterTypeService();

    /**
     * The table that displays students.
     * Assigned by {@link #buildTable()}; {@code null} until {@link #show()} is called.
     */
    private TableView<Student> table;

    /**
     * The backing list for the table.
     *
     * <p>Initialised here (not lazily inside {@code buildTable()}) so that
     * {@link #refreshTable()} is safe to call even when {@link #show()} has not
     * been called first — which happens when the Dashboard opens the Add-Student
     * dialog via {@link #showAddDialog()}.
     */
    private final ObservableList<Student> data = FXCollections.observableArrayList();

    /**
     * Constructs a new {@code StudentView}.
     *
     * @param stage     the main application window
     * @param dashboard the dashboard view (used for navigation and nav-button style)
     */
    public StudentView(Stage stage, DashboardView dashboard) {
        this.stage     = stage;
        this.dashboard = dashboard;
    }

    /**
     * Builds and displays the student-list scene on the main stage.
     *
     * <p>Call order inside this method matters:
     * {@code buildContent()} → {@code buildTable()} (links {@code data} to the
     * {@link TableView}) → {@code refreshTable()} (populates the list).
     */
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle(MonsterHighStyles.SCENE_BG);
        root.setLeft(buildSidebar());
        root.setCenter(buildContent()); // creates the TableView and wires it to 'data'

        stage.setScene(new Scene(root, 1100, 700));
        stage.setTitle("Student Management — Monster High");

        refreshTable(); // safe: 'data' was initialised at declaration time
    }

    /**
     * Opens the Add-Student dialog directly, without building the full scene first.
     *
     * <p>This method is called by the Dashboard quick-action button.
     * Because {@code data} is initialised eagerly, the subsequent
     * {@link #refreshTable()} call inside the dialog's Save handler is safe.
     */
    public void showAddDialog() {
        openDialog(null); // null = new student
    }

    /**
     * Builds the left navigation sidebar with logo and nav buttons.
     *
     * @return the {@link VBox} containing the sidebar content
     */
    private VBox buildSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(220);
        sidebar.setPadding(new Insets(24, 12, 24, 12));
        sidebar.setStyle("-fx-background-color: #12122A;");

        Label skull = new Label("💀");
        skull.setStyle("-fx-font-size: 40px;");
        Label brand = new Label("Monster High");
        brand.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: "
                + MonsterHighStyles.PINK + ";");

        VBox logo = new VBox(4, skull, brand);
        logo.setAlignment(Pos.CENTER);
        logo.setPadding(new Insets(0, 0, 20, 0));

        Region divider = new Region();
        divider.setStyle(MonsterHighStyles.DIVIDER);

        // "Students" is the active section
        Button btnDash     = dashboard.navButton("🏠  Dashboard",   false);
        Button btnStudents = dashboard.navButton("🧑‍🎓  Students",    true);
        Button btnTeachers = dashboard.navButton("🧙  Teachers",    false);
        Button btnSubjects = dashboard.navButton("📚  Subjects",    false);
        Button btnEnroll   = dashboard.navButton("📋  Enrollments", false);

        btnDash.setOnAction(e -> dashboard.show());
        btnTeachers.setOnAction(e -> new TeacherView(stage, dashboard).show());
        btnSubjects.setOnAction(e -> new SubjectView(stage, dashboard).show());
        btnEnroll.setOnAction(e -> new EnrollmentView(stage, dashboard).show());

        sidebar.getChildren().addAll(logo, divider,
                btnDash, btnStudents, btnTeachers, btnSubjects, btnEnroll);
        return sidebar;
    }

    /**
     * Builds the toolbar (search + buttons) and the student table.
     *
     * <p>This method calls {@link #buildTable()}, which wires the shared
     * {@code data} list to the {@link TableView}.
     *
     * @return the {@link VBox} with the title, toolbar and table
     */
    private VBox buildContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(28));

        Label title = new Label("🧟 Student Management");
        title.setStyle(MonsterHighStyles.TITLE);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or ID...");
        searchField.setStyle(MonsterHighStyles.TEXT_FIELD);
        searchField.setPrefWidth(260);

        Button btnSearch = new Button("🔍 Search");
        btnSearch.setStyle(MonsterHighStyles.BTN_GHOST);
        btnSearch.setOnAction(e -> filterTable(searchField.getText()));
        searchField.setOnAction(e -> filterTable(searchField.getText())); // Enter key

        Button btnAdd = new Button("➕ Add Student");
        btnAdd.setStyle(MonsterHighStyles.BTN_PRIMARY);
        btnAdd.setOnAction(e -> showAddDialog());

        Button btnRefresh = new Button("🔄 Refresh");
        btnRefresh.setStyle(MonsterHighStyles.BTN_SECONDARY);
        btnRefresh.setOnAction(e -> refreshTable());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, searchField, btnSearch, spacer, btnAdd, btnRefresh);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        table = buildTable(); // wires 'data' to the TableView
        VBox.setVgrow(table, Priority.ALWAYS);

        content.getChildren().addAll(title, toolbar, table);
        return content;
    }

    /**
     * Builds the {@link TableView} with all columns and the per-row Edit/Delete buttons,
     * then sets its items to the shared {@code data} observable list.
     *
     * @return the fully configured {@link TableView}
     */
    @SuppressWarnings("unchecked")
    private TableView<Student> buildTable() {
        TableView<Student> tv = new TableView<>();
        tv.setStyle(MonsterHighStyles.TABLE);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPlaceholder(new Label("No students found 🕸️"));

        // ID column
        TableColumn<Student, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colId.setPrefWidth(50);

        // Name column
        TableColumn<Student, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName()));

        // Surname column
        TableColumn<Student, String> colSurname = new TableColumn<>("Surname");
        colSurname.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getSurname()));

        // Email column
        TableColumn<Student, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEmail()));

        // Year column
        TableColumn<Student, String> colYear = new TableColumn<>("Year");
        colYear.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getStudentYear())));
        colYear.setPrefWidth(55);

        // Group column
        TableColumn<Student, String> colGroup = new TableColumn<>("Group");
        colGroup.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getGroupName()));
        colGroup.setPrefWidth(70);

        // Monster type column — shows "-" when no type is assigned
        TableColumn<Student, String> colMonster = new TableColumn<>("Monster Type");
        colMonster.setCellValueFactory(c -> {
            MonsterType mt = c.getValue().getMonsterType();
            return new SimpleStringProperty(mt != null ? mt.getName() : "-");
        });

        // Actions column: Edit and Delete buttons per row
        TableColumn<Student, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(160);
        colActions.setCellFactory(col -> new TableCell<Student, Void>() {
            private final Button btnEdit   = new Button("✏️ Edit");
            private final Button btnDelete = new Button("🗑️ Delete");

            {
                btnEdit.setStyle(MonsterHighStyles.BTN_SECONDARY);
                btnDelete.setStyle(MonsterHighStyles.BTN_DANGER);

                btnEdit.setOnAction(e -> {
                    Student s = getTableView().getItems().get(getIndex());
                    openDialog(s);
                });
                btnDelete.setOnAction(e -> {
                    Student s = getTableView().getItems().get(getIndex());
                    confirmDelete(s);
                });
            }

            /** {@inheritDoc} */
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(6, btnEdit, btnDelete);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        tv.getColumns().addAll(colId, colName, colSurname, colEmail,
                colYear, colGroup, colMonster, colActions);

        // Link the shared 'data' list to this TableView
        tv.setItems(data);
        return tv;
    }

    /**
     * Opens a modal dialog to create ({@code existing == null}) or edit a student.
     *
     * <p>All validation is performed before calling the service layer.
     * If validation fails, an inline error message is shown inside the dialog
     * and no data is written — the dialog stays open for correction.
     *
     * <p>On success the dialog closes and the table is refreshed.
     *
     * @param existing the student to edit, or {@code null} to create a new one
     */
    private void openDialog(Student existing) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle(existing == null ? "Add Student" : "Edit Student #" + existing.getId());

        VBox form = new VBox(12);
        form.setPadding(new Insets(24));
        form.setStyle(MonsterHighStyles.SCENE_BG);
        form.setPrefWidth(440);

        Label heading = new Label(existing == null ? "➕ New Student" : "✏️ Edit Student");
        heading.setStyle(MonsterHighStyles.HEADING);

        // Form fields — pre-filled when editing
        TextField tfName    = styledField("Name",          existing != null ? existing.getName()      : "");
        TextField tfSurname = styledField("Surname",       existing != null ? existing.getSurname()   : "");
        TextField tfEmail   = styledField("Email",         existing != null ? existing.getEmail()      : "");
        TextField tfYear    = styledField("Year (1 or 2)", existing != null
                ? String.valueOf(existing.getStudentYear()) : "");
        TextField tfGroup   = styledField("Group (e.g. 1A)", existing != null ? existing.getGroupName() : "");

        DatePicker dpBirth = new DatePicker();
        dpBirth.setStyle(MonsterHighStyles.COMBO);
        dpBirth.setValue(existing != null ? existing.getBirthDate() : LocalDate.now().minusYears(16));

        // Load monster types; empty list on failure (combo simply shows nothing)
        List<MonsterType> types = safeList(() -> monsterTypeService.findAll());
        ComboBox<MonsterType> cbMonster = new ComboBox<>(FXCollections.observableArrayList(types));
        cbMonster.setStyle(MonsterHighStyles.COMBO);
        cbMonster.setMaxWidth(Double.MAX_VALUE);
        cbMonster.setConverter(new javafx.util.StringConverter<MonsterType>() {
            /** {@inheritDoc} */
            @Override public String toString(MonsterType mt)       { return mt == null ? "" : mt.getName(); }
            /** {@inheritDoc} */
            @Override public MonsterType fromString(String s)      { return null; }
        });

        // Pre-select the current monster type when editing
        if (existing != null && existing.getMonsterType() != null) {
            types.stream()
                 .filter(mt -> mt.getId() == existing.getMonsterType().getId())
                 .findFirst()
                 .ifPresent(cbMonster::setValue);
        }

        // Inline error label — shown in red when validation fails
        Label lblError = new Label("");
        lblError.setStyle("-fx-text-fill: " + MonsterHighStyles.RED + "; -fx-font-size: 12px;");
        lblError.setWrapText(true);

        Button btnSave   = new Button("💾 Save");
        Button btnCancel = new Button("Cancel");
        btnSave.setStyle(MonsterHighStyles.BTN_PRIMARY);
        btnCancel.setStyle(MonsterHighStyles.BTN_GHOST);

        btnSave.setOnAction(e -> {
            lblError.setText(""); // clear any previous error

            String name    = tfName.getText().trim();
            String surname = tfSurname.getText().trim();
            String email   = tfEmail.getText().trim();
            String group   = tfGroup.getText().trim();
            if (name.isEmpty()) {
                lblError.setText("Name is required.");
                return;
            }
            if (surname.isEmpty()) {
                lblError.setText("Surname is required.");
                return;
            }
            if (email.isEmpty() || !email.contains("@")) {
                lblError.setText("A valid email address is required.");
                return;
            }
            if (group.isEmpty()) {
                lblError.setText("Group is required (e.g. 1A).");
                return;
            }

            int year;
            try {
                year = Integer.parseInt(tfYear.getText().trim());
            } catch (NumberFormatException ex) {
                lblError.setText("Year must be a number (1 or 2).");
                return;
            }
            if (year < 1 || year > 2) {
                lblError.setText("Year must be 1 or 2.");
                return;
            }

            MonsterType mt = cbMonster.getValue();
            if (mt == null) {
                lblError.setText("Please select a Monster Type.");
                return;
            }

            LocalDate bd = dpBirth.getValue();
            if (bd == null) {
                lblError.setText("Birth date is required.");
                return;
            }
            try {
                if (existing == null) {
                    int newId = studentService.findAll().stream()
                            .mapToInt(Student::getId).max().orElse(0) + 1;
                    studentService.save(new Student(newId, name, surname, bd, email, year, group, mt));
                } else {
                    studentService.update(
                            new Student(existing.getId(), name, surname, bd, email, year, group, mt));
                }
                dialog.close();
                refreshTable(); // safe: 'data' is always initialised
            } catch (Exception ex) {
                lblError.setText("Error saving student: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        HBox btnRow = new HBox(10, btnSave, btnCancel);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        form.getChildren().addAll(
                heading,
                fieldRow("Name",         tfName),
                fieldRow("Surname",      tfSurname),
                fieldRow("Email",        tfEmail),
                fieldRow("Year",         tfYear),
                fieldRow("Group",        tfGroup),
                fieldRow("Birth date",   dpBirth),
                fieldRow("Monster type", cbMonster),
                lblError,
                btnRow
        );

        dialog.setScene(new Scene(form));
        dialog.showAndWait();
    }

    /**
     * Shows a confirmation alert before deleting the given student.
     * Deletes and refreshes the table only if the user confirms.
     *
     * @param s the student to delete
     */
    private void confirmDelete(Student s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + s.getName() + " " + s.getSurname() + "?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm deletion");
        alert.setHeaderText("This action cannot be undone.");

        alert.showAndWait().ifPresent(answer -> {
            if (answer == ButtonType.YES) {
                try {
                    studentService.deleteStudent(s.getId());
                    refreshTable();
                } catch (StudentNotFoundException ex) {
                    showError("Could not delete: " + ex.getMessage());
                }
            }
        });
    }

    /**
     * Loads all students from the service into the shared {@code data} list.
     *
     * <p>Safe to call at any time because {@code data} is initialised at
     * field-declaration time (not inside {@link #buildTable()}).
     */
    private void refreshTable() {
        try {
            data.setAll(studentService.findAll());
        } catch (Exception ex) {
            showError("Could not load students: " + ex.getMessage());
        }
    }

    /**
     * Filters the table to show only students whose name, surname or ID
     * contains the given query string (case-insensitive).
     * An empty or blank query restores the full list.
     *
     * @param query the search string typed by the user
     */
    private void filterTable(String query) {
        if (query == null || query.isBlank()) {
            refreshTable();
            return;
        }
        String q = query.toLowerCase();
        try {
            List<Student> filtered = studentService.findAll().stream()
                    .filter(s -> s.getName().toLowerCase().contains(q)
                              || s.getSurname().toLowerCase().contains(q)
                              || String.valueOf(s.getId()).contains(q))
                    .toList();
            data.setAll(filtered);
        } catch (Exception ex) {
            showError("Search error: " + ex.getMessage());
        }
    }

    /**
     * Creates a styled {@link TextField} with the given prompt and initial value.
     *
     * @param prompt placeholder text shown when the field is empty
     * @param value  initial text content
     * @return the configured {@link TextField}
     */
    private TextField styledField(String prompt, String value) {
        TextField tf = new TextField(value);
        tf.setPromptText(prompt);
        tf.setStyle(MonsterHighStyles.TEXT_FIELD);
        return tf;
    }

    /**
     * Creates a horizontal label + control row for the form layout.
     *
     * @param label   descriptive text shown on the left
     * @param control the input control placed on the right
     * @return an {@link HBox} containing the label and control
     */
    private HBox fieldRow(String label, javafx.scene.Node control) {
        Label lbl = new Label(label + ":");
        lbl.setStyle(MonsterHighStyles.MUTED);
        lbl.setPrefWidth(110);
        HBox.setHgrow(control, Priority.ALWAYS);
        HBox row = new HBox(10, lbl, control);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * Calls a {@link java.util.concurrent.Callable} that returns a list and
     * returns an empty list on any exception.
     * Used to load combo-box data without crashing the dialog.
     *
     * @param <T>      element type
     * @param supplier the callable that provides the list
     * @return the list returned by the supplier, or {@link List#of()} on error
     */
    private <T> List<T> safeList(java.util.concurrent.Callable<List<T>> supplier) {
        try {
            return supplier.call();
        } catch (Exception ex) {
            return List.of();
        }
    }

    /**
     * Shows a simple error {@link Alert} with the given message.
     *
     * @param msg the error message to display
     */
    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
