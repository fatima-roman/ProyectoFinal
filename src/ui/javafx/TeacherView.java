package ui.javafx;

import model.Teacher;
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

import java.time.LocalDate;
import java.util.List;

/**
 * View for Teacher Management in the Monster High Institute Manager.
 *
 * <p>Displays all teachers in a {@link TableView} and allows the user to:
 * <ul>
 *   <li>Search teachers by name</li>
 *   <li>Add a new teacher via a modal dialog (also callable from the Dashboard)</li>
 *   <li>Edit an existing teacher via a pre-filled modal dialog</li>
 *   <li>Delete a teacher after confirmation</li>
 * </ul>
 *
 *
 * @author Fatima Roman
 * @version 1.1
 */
public class TeacherView {

    /** The main JavaFX window. */
    private final Stage stage;

    /** Reference to the dashboard, used for navigation and nav-button style. */
    private final DashboardView dashboard;

    /** Service layer for teacher persistence operations. */
    private final TeacherService teacherService = new TeacherService();

    /**
     * The table that displays teachers.
     * Assigned by {@link #buildTable()}; may be {@code null} if {@link #show()} has
     * not been called (e.g. when opened via {@link #showAddDialog()} from the Dashboard).
     */
    private TableView<Teacher> table;

    /**
     * The backing list for the table.
     *
     * <p>Initialised eagerly so that {@link #refreshTable()} is safe to call
     * even when the full scene has not been built yet.
     */
    private final ObservableList<Teacher> data = FXCollections.observableArrayList();

    /**
     * Constructs a new {@code TeacherView}.
     *
     * @param stage     the main application window
     * @param dashboard the dashboard view (used for navigation)
     */
    public TeacherView(Stage stage, DashboardView dashboard) {
        this.stage     = stage;
        this.dashboard = dashboard;
    }
    /**
     * Builds and displays the teacher-list scene on the main stage.
     */
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle(MonsterHighStyles.SCENE_BG);
        root.setLeft(buildSidebar());
        root.setCenter(buildContent()); // wires the TableView to 'data'

        stage.setScene(new Scene(root, 1100, 700));
        stage.setTitle("Teacher Management — Monster High");

        refreshTable(); // always safe because 'data' is eager
    }

    /**
     * Opens the Add-Teacher dialog directly, without building the full scene.
     *
     * <p>Called by the Dashboard quick-action button.
     */
    public void showAddDialog() {
        openDialog(null); // null = new teacher
    }

    /**
     * Builds the left navigation sidebar.
     *
     * @return the {@link VBox} containing logo and nav buttons
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

        // "Teachers" is the active section
        Button btnDash     = dashboard.navButton("🏠  Dashboard",   false);
        Button btnStudents = dashboard.navButton("🧟  Students",    false);
        Button btnTeachers = dashboard.navButton("🧙  Teachers",    true);
        Button btnSubjects = dashboard.navButton("📚  Subjects",    false);
        Button btnEnroll   = dashboard.navButton("📋  Enrollments", false);

        btnDash.setOnAction(e -> dashboard.show());
        btnStudents.setOnAction(e -> new StudentView(stage, dashboard).show());
        btnSubjects.setOnAction(e -> new SubjectView(stage, dashboard).show());
        btnEnroll.setOnAction(e -> new EnrollmentView(stage, dashboard).show());

        sidebar.getChildren().addAll(logo, divider,
                btnDash, btnStudents, btnTeachers, btnSubjects, btnEnroll);
        return sidebar;
    }

    /**
     * Builds the toolbar and the teacher table.
     *
     * @return the {@link VBox} with the title, toolbar and table
     */
    private VBox buildContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(28));

        Label title = new Label("🧙 Teacher Management");
        title.setStyle(MonsterHighStyles.TITLE);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name...");
        searchField.setStyle(MonsterHighStyles.TEXT_FIELD);
        searchField.setPrefWidth(260);

        Button btnSearch = new Button("🔍 Search");
        btnSearch.setStyle(MonsterHighStyles.BTN_GHOST);
        btnSearch.setOnAction(e -> filterTable(searchField.getText()));
        searchField.setOnAction(e -> filterTable(searchField.getText()));

        Button btnAdd = new Button("➕ Add Teacher");
        btnAdd.setStyle(MonsterHighStyles.BTN_PRIMARY);
        btnAdd.setOnAction(e -> showAddDialog());

        Button btnRefresh = new Button("🔄 Refresh");
        btnRefresh.setStyle(MonsterHighStyles.BTN_SECONDARY);
        btnRefresh.setOnAction(e -> refreshTable());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, searchField, btnSearch, spacer, btnAdd, btnRefresh);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        content.getChildren().addAll(title, toolbar, table);
        return content;
    }

    /**
     * Builds the {@link TableView} with all columns and per-row action buttons,
     * then links it to the shared {@code data} list.
     *
     * @return the fully configured {@link TableView}
     */
    @SuppressWarnings("unchecked")
    private TableView<Teacher> buildTable() {
        TableView<Teacher> tv = new TableView<>();
        tv.setStyle(MonsterHighStyles.TABLE);
        tv.setPlaceholder(new Label("No teachers registered 🕸️"));

        TableColumn<Teacher, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colId.setPrefWidth(50);

        TableColumn<Teacher, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName()));

        TableColumn<Teacher, String> colSurname = new TableColumn<>("Surname");
        colSurname.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getSurname()));

        TableColumn<Teacher, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEmail()));

        TableColumn<Teacher, String> colSpecialty = new TableColumn<>("Specialty");
        colSpecialty.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getSpecialty()));

        // Per-row Edit + Delete buttons
        TableColumn<Teacher, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(160);
        colActions.setCellFactory(col -> new TableCell<Teacher, Void>() {
            private final Button btnEdit   = new Button("✏️ Edit");
            private final Button btnDelete = new Button("🗑️ Delete");

            {
                btnEdit.setStyle(MonsterHighStyles.BTN_SECONDARY);
                btnDelete.setStyle(MonsterHighStyles.BTN_DANGER);
                btnEdit.setOnAction(e   -> openDialog(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> confirmDelete(getTableView().getItems().get(getIndex())));
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

        tv.getColumns().addAll(colId, colName, colSurname, colEmail, colSpecialty, colActions);

        // Link the shared 'data' list to this TableView
        tv.setItems(data);
        return tv;
    }

    /**
     * Opens a modal dialog to create ({@code existing == null}) or edit a teacher.
     *
     * <p>Validation runs before any service call; errors are displayed inline
     * and the dialog stays open for correction.
     *
     * @param existing the teacher to edit, or {@code null} to create a new one
     */
    private void openDialog(Teacher existing) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle(existing == null ? "Add Teacher" : "Edit Teacher #" + existing.getId());

        VBox form = new VBox(12);
        form.setPadding(new Insets(24));
        form.setStyle(MonsterHighStyles.SCENE_BG);
        form.setPrefWidth(420);

        Label heading = new Label(existing == null ? "➕ New Teacher" : "✏️ Edit Teacher");
        heading.setStyle(MonsterHighStyles.HEADING);

        TextField tfName      = sf("Name",      existing != null ? existing.getName()      : "");
        TextField tfSurname   = sf("Surname",   existing != null ? existing.getSurname()   : "");
        TextField tfEmail     = sf("Email",     existing != null ? existing.getEmail()      : "");
        TextField tfSpecialty = sf("Specialty", existing != null ? existing.getSpecialty() : "");

        DatePicker dp = new DatePicker();
        dp.setStyle(MonsterHighStyles.COMBO);
        dp.setValue(existing != null ? existing.getBirthDate() : LocalDate.now().minusYears(30));

        // Inline error label
        Label lblError = new Label("");
        lblError.setStyle("-fx-text-fill: " + MonsterHighStyles.RED + "; -fx-font-size: 12px;");
        lblError.setWrapText(true);

        Button btnSave   = new Button("💾 Save");
        Button btnCancel = new Button("Cancel");
        btnSave.setStyle(MonsterHighStyles.BTN_PRIMARY);
        btnCancel.setStyle(MonsterHighStyles.BTN_GHOST);
        btnSave.setOnAction(e -> {
            lblError.setText("");

            String name      = tfName.getText().trim();
            String surname   = tfSurname.getText().trim();
            String email     = tfEmail.getText().trim();
            String specialty = tfSpecialty.getText().trim();
            LocalDate bd     = dp.getValue();

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
            if (bd == null) {
                lblError.setText("Birth date is required.");
                return;
            }

            try {
                if (existing == null) {
                    int newId = teacherService.findAll().stream()
                            .mapToInt(Teacher::getId).max().orElse(0) + 1;
                    teacherService.save(new Teacher(newId, name, surname, bd, email, specialty));
                } else {
                    teacherService.update(
                            new Teacher(existing.getId(), name, surname, bd, email, specialty));
                }
                dialog.close();
                refreshTable();
            } catch (Exception ex) {
                lblError.setText("Error saving teacher: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        HBox btnRow = new HBox(10, btnSave, btnCancel);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        form.getChildren().addAll(heading,
                fr("Name",       tfName),
                fr("Surname",    tfSurname),
                fr("Email",      tfEmail),
                fr("Specialty",  tfSpecialty),
                fr("Birth date", dp),
                lblError, btnRow);

        dialog.setScene(new Scene(form));
        dialog.showAndWait();
    }

    /**
     * Shows a confirmation alert before deleting the given teacher.
     *
     * @param t the teacher to delete
     */
    private void confirmDelete(Teacher t) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + t.getName() + " " + t.getSurname() + "?",
                ButtonType.YES, ButtonType.NO);
        a.setTitle("Confirm deletion");
        a.setHeaderText("This action cannot be undone.");
        a.showAndWait().ifPresent(answer -> {
            if (answer == ButtonType.YES) {
                try {
                    teacherService.deleteTeacher(t.getId());
                    refreshTable();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Could not delete: " + ex.getMessage()).showAndWait();
                }
            }
        });
    }

    /**
     * Loads all teachers from the service into {@code data}.
     * Always safe because {@code data} is initialised at declaration time.
     */
    private void refreshTable() {
        try {
            data.setAll(teacherService.findAll());
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Could not load teachers: " + ex.getMessage()).showAndWait();
        }
    }

    /**
     * Filters the table to show only teachers whose name or surname contains
     * {@code q} (case-insensitive). An empty query restores the full list.
     *
     * @param q the search string
     */
    private void filterTable(String q) {
        if (q == null || q.isBlank()) { refreshTable(); return; }
        String ql = q.toLowerCase();
        try {
            data.setAll(teacherService.findAll().stream()
                    .filter(t -> t.getName().toLowerCase().contains(ql)
                              || t.getSurname().toLowerCase().contains(ql))
                    .toList());
        } catch (Exception ex) {}
    }

    /**
     * Creates a styled {@link TextField} with prompt and initial value.
     *
     * @param prompt placeholder text
     * @param val    initial value
     * @return the configured {@link TextField}
     */
    private TextField sf(String prompt, String val) {
        TextField tf = new TextField(val);
        tf.setPromptText(prompt);
        tf.setStyle(MonsterHighStyles.TEXT_FIELD);
        return tf;
    }

    /**
     * Creates a label + control form row.
     *
     * @param label descriptive label on the left
     * @param ctrl  input control on the right
     * @return the {@link HBox} row
     */
    private HBox fr(String label, javafx.scene.Node ctrl) {
        Label l = new Label(label + ":");
        l.setStyle(MonsterHighStyles.MUTED);
        l.setPrefWidth(100);
        HBox.setHgrow(ctrl, Priority.ALWAYS);
        HBox row = new HBox(10, l, ctrl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }
}
