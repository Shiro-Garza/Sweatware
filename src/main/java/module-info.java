module edu.utsa.cs3443.sweatware_alpha {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.httpserver;
    requires java.desktop;

    opens edu.utsa.cs3443.sweatware_alpha to javafx.fxml;
    exports edu.utsa.cs3443.sweatware_alpha;

    opens edu.utsa.cs3443.sweatware_alpha.controller to javafx.fxml;
    exports edu.utsa.cs3443.sweatware_alpha.controller;

    opens edu.utsa.cs3443.sweatware_alpha.model to javafx.fxml;
    exports edu.utsa.cs3443.sweatware_alpha.model;
}