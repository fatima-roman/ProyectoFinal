package ui.javafx;

import model.Subject;
import model.Teacher;
import service.SubjectService;
import service.TeacherService;

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

import java.util.List;

/**
 * View for Subject Management.
 *
 * Shows all subjects in a table with Edit and Delete actions,
 * and allows assigning a teacher to each subject via a ComboBox.
 *
 * Subject model uses:
 *   - getCourse()    (NOT getCourseYear)
 *   - getTeacher()   (returns a Teacher object, NOT an int teacherId)
 *   - Constructor: Subject(id, name, course, Teacher)
 * @author Fatima Roman
 * @version 1.0
 */
public class SubjectView {

    private Stage stage;
    private DashboardView dashboard;

    private SubjectService subjectService = new SubjectService();
    private TeacherService teacherService = new TeacherService();

    // Assigned by buildTable() — do not call refreshTable() before buildContent()
    private TableView<Subject>       table;
    private ObservableList<Subject>  data;

    /**
     * Creates the Subject view.
     *
     * @param stage     the main window
     * @param dashboard reference to the dashboard
     */
    public SubjectView(Stage stage, DashboardView dashboard) {
        this.stage     = stage;
        this.dashboard = dashboard;
    }

    /**
     * Builds and shows the subject list on the main stage.
     */
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle(MonsterHighStyles.SCENE_BG);
        root.setLeft(buildSidebar());
        root.setCenter(buildContent()); // assigns 'data' via buildTable()

        stage.setScene(new Scene(root, 1100, 700));
        stage.setTitle("Subject Management — Monster High");

        refreshTable(); // safe now because 'data' is ready
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(220);
        sidebar.setPadding(new Insets(24, 12, 24, 12));
        sidebar.setStyle("-fx-background-color: #12122A;");

        Label skull = new Label("💀");
        skull.setStyle("-fx-font-size: 40px;");
        Label brand = new Label("Monster High");
        brand.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + MonsterHighStyles.PINK + ";");

        VBox logo = new VBox(4, skull, brand);
        logo.setAlignment(Pos.CENTER);
        logo.setPadding(new Insets(0, 0, 20, 0));

        Region divider = new Region();
        divider.setStyle(MonsterHighStyles.DIVIDER);

        Button btnDash     = dashboard.navButton("🏠  Dashboard",    false);
        Button btnStudents = dashboard.navButton("🧟  Students",     false);
        Button btnTeachers = dashboard.navButton("🧙  Teachers",     false);
        Button btnSubjects = dashboard.navButton("📚  Subjects",     true);  // active
        Button btnEnroll   = dashboard.navButton("📋  Enrollments",  false);

        btnDash.setOnAction(e -> dashboard.show());
        btnStudents.setOnAction(e -> new StudentView(stage, dashboard).show());
        btnTeachers.setOnAction(e -> new TeacherView(stage, dashboard).show());
        btnEnroll.setOnAction(e -> new EnrollmentView(stage, dashboard).show());

        sidebar.getChildren().addAll(logo, divider,
            btnDash, btnStudents, btnTeachers, btnSubjects, btnEnroll);
        return sidebar;
    }

    /**
     * Builds the toolbar and the table.
     * Calling this method initialises 'data' via buildTable().
     *
     * @return the VBox with the full content area
     */
    private VBox buildContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(28));

        Label title = new Label("📚 Subject Management");
        title.setStyle(MonsterHighStyles.TITLE);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name...");
        searchField.setStyle(MonsterHighStyles.TEXT_FIELD);
        searchField.setPrefWidth(240);

        Button btnSearch = new Button("🔍 Search");
        btnSearch.setStyle(MonsterHighStyles.BTN_GHOST);
        btnSearch.setOnAction(e -> filterTable(searchField.getText()));
        searchField.setOnAction(e -> filterTable(searchField.getText()));

        Button btnAdd = new Button("➕ Add Subject");
        btnAdd.setStyle(MonsterHighStyles.BTN_PRIMARY);
        btnAdd.setOnAction(e -> showAddDialog());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, searchField, btnSearch, spacer, btnAdd);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // buildTable() initialises 'data' — must be before refreshTable()
        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        content.getChildren().addAll(title, toolbar, table);
        return content;
    }

    /**
     * Builds the TableView and assigns the 'data' observable list.
     *
     * Uses getCourse() and getTeacher() — the actual getters from Subject.java.
     *
     * @return the configured TableView
     */
    @SuppressWarnings("unchecked")
    private TableView<Subject> buildTable() {
        TableView<Subject> tv = new TableView<>();
        tv.setStyle(MonsterHighStyles.TABLE);
        tv.setPlaceholder(new Label("No subjects registered 🕸️"));

        TableColumn<Subject, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c ->
            new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colId.setPrefWidth(50);

        TableColumn<Subject, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getName()));

        // getCourse() — NOT getCourseYear()
        TableColumn<Subject, String> colYear = new TableColumn<>("Year");
        colYear.setCellValueFactory(c ->
            new SimpleStringProperty(String.valueOf(c.getValue().getCourse())));
        colYear.setPrefWidth(55);

        // getTeacher() returns a Teacher object — we show name + surname (or "-" if null)
        TableColumn<Subject, String> colTeacher = new TableColumn<>("Teacher");
        colTeacher.setCellValueFactory(c -> {
            Teacher t = c.getValue().getTeacher();
            return new SimpleStringProperty(t != null ? t.getName() + " " + t.getSurname() : "-");
        });

        // Actions column: Edit + Delete per row
        TableColumn<Subject, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(160);
        colActions.setCellFactory(col -> new TableCell<Subject, Void>() {
            private final Button btnEdit   = new Button("✏️ Edit");
            private final Button btnDelete = new Button("🗑️ Delete");

            {
                btnEdit.setStyle(MonsterHighStyles.BTN_SECONDARY);
                btnDelete.setStyle(MonsterHighStyles.BTN_DANGER);

                btnEdit.setOnAction(e -> openDialog(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> confirmDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : new HBox(6, btnEdit, btnDelete));
            }
        });

        tv.getColumns().addAll(colId, colName, colYear, colTeacher, colActions);

        // Create and link the data list
        data = FXCollections.observableArrayList();
        tv.setItems(data);
        return tv;
    }


    /**
     * Opens the Add Subject dialog.
     * Called from the dashboard quick-action button too.
     */
    public void showAddDialog() {
        openDialog(null);
    }

    /**
     * Opens a modal dialog to create or edit a subject.
     *
     * Constructor used: Subject(int id, String name, int course, Teacher teacher)
     *
     * @param existing the subject to edit, or null to create a new one
     */
    private void openDialog(Subject existing) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle(existing == null ? "Add Subject" : "Edit Subject #" + existing.getId());

        VBox form = new VBox(12);
        form.setPadding(new Insets(24));
        form.setStyle(MonsterHighStyles.SCENE_BG);
        form.setPrefWidth(400);

        Label heading = new Label(existing == null ? "➕ New Subject" : "✏️ Edit Subject");
        heading.setStyle(MonsterHighStyles.HEADING);

        TextField tfName = sf("Subject name", existing != null ? existing.getName() : "");
        // getCourse() — the real getter name
        TextField tfYear = sf("Year (1 or 2)", existing != null ? String.valueOf(existing.getCourse()) : "");

        // Load all teachers for the combo box
        List<Teacher> teachers = safeList(() -> teacherService.findAll());
        ComboBox<Teacher> cbTeacher = new ComboBox<>(FXCollections.observableArrayList(teachers));
        cbTeacher.setStyle(MonsterHighStyles.COMBO);
        cbTeacher.setMaxWidth(Double.MAX_VALUE);
        cbTeacher.setConverter(new javafx.util.StringConverter<Teacher>() {
            @Override public String toString(Teacher t)   { return t == null ? "" : t.getName() + " " + t.getSurname(); }
            @Override public Teacher fromString(String s) { return null; }
        });

        // Pre-select the current teacher when editing
        // getTeacher() returns the Teacher object directly — we compare by id
        if (existing != null && existing.getTeacher() != null) {
            for (Teacher t : teachers) {
                if (t.getId() == existing.getTeacher().getId()) {
                    cbTeacher.setValue(t);
                    break;
                }
            }
        }

        Label lblError = new Label("");
        lblError.setStyle("-fx-text-fill: " + MonsterHighStyles.RED + ";");

        Button btnSave   = new Button("💾 Save");
        Button btnCancel = new Button("Cancel");
        btnSave.setStyle(MonsterHighStyles.BTN_PRIMARY);
        btnCancel.setStyle(MonsterHighStyles.BTN_GHOST);

        btnSave.setOnAction(e -> {
            try {
                String name = tfName.getText().trim();
                int year    = Integer.parseInt(tfYear.getText().trim());
                Teacher tch = cbTeacher.getValue();

                if (name.isEmpty()) throw new IllegalArgumentException("Name is required.");
                if (year < 1 || year > 2) throw new IllegalArgumentException("Year must be 1 or 2.");
                if (tch == null) throw new IllegalArgumentException("Please select a teacher.");

                // Constructor: Subject(int id, String name, int course, Teacher teacher)
                // We pass the Teacher object, NOT an int id
                if (existing == null) {
                    int newId = subjectService.findAll().stream()
                        .mapToInt(Subject::getId).max().orElse(0) + 1;
                    subjectService.save(new Subject(newId, name, year, tch));
                } else {
                    subjectService.update(new Subject(existing.getId(), name, year, tch));
                }

                dialog.close();
                refreshTable();

            } catch (NumberFormatException ex) {
                lblError.setText("Year must be a valid number.");
            } catch (Exception ex) {
                lblError.setText("Error: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        HBox btnRow = new HBox(10, btnSave, btnCancel);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        form.getChildren().addAll(heading,
            fr("Name",    tfName),
            fr("Year",    tfYear),
            fr("Teacher", cbTeacher),
            lblError, btnRow);

        dialog.setScene(new Scene(form));
        dialog.showAndWait();
    }

    private void confirmDelete(Subject s) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete subject \"" + s.getName() + "\"?", ButtonType.YES, ButtonType.NO);
        a.showAndWait().ifPresent(answer -> {
            if (answer == ButtonType.YES) {
                try {
                    subjectService.deleteSubject(s.getId());
                    refreshTable();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                }
            }
        });
    }

    /**
     * Loads all subjects from the service into the table.
     * Must only be called after buildTable() has initialised 'data'.
     */
    private void refreshTable() {
        try {
            data.setAll(subjectService.findAll());
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Could not load subjects.").showAndWait();
        }
    }

    private void filterTable(String q) {
        if (q == null || q.isBlank()) { refreshTable(); return; }
        String ql = q.toLowerCase();
        try {
            data.setAll(subjectService.findAll().stream()
                .filter(s -> s.getName().toLowerCase().contains(ql))
                .toList());
        } catch (Exception ex) {}
    }

    private TextField sf(String prompt, String val) {
        TextField tf = new TextField(val);
        tf.setPromptText(prompt);
        tf.setStyle(MonsterHighStyles.TEXT_FIELD);
        return tf;
    }

    private HBox fr(String label, javafx.scene.Node ctrl) {
        Label l = new Label(label + ":");
        l.setStyle(MonsterHighStyles.MUTED);
        l.setPrefWidth(100);
        HBox.setHgrow(ctrl, Priority.ALWAYS);
        HBox row = new HBox(10, l, ctrl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private <T> List<T> safeList(java.util.concurrent.Callable<List<T>> supplier) {
        try { return supplier.call(); }
        catch (Exception ex) { return List.of(); }
    }
}
