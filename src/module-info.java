module Proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens ui.javafx to javafx.fxml, javafx.graphics, javafx.base;
    opens ui to javafx.base;
    exports ui.javafx;
    exports ui;
}