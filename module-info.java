module Proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires sqlite.jdbc;

    opens ui.javafx to javafx.fxml, javafx.graphics;
    exports ui.javafx;
}