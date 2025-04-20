//package application;
//
//import javafx.scene.control.ScrollPane;
//import javafx.scene.control.Tab;
//import javafx.scene.control.TabPane;
//import javafx.scene.layout.BorderPane;
//
//public class UIManager {
//    private TabPane tabPane;
//
//    public BorderPane createMainLayout() {
//        BorderPane root = new BorderPane();
//        tabPane = new TabPane();
//
//        // Add tabs in logical order
//        Tab buildingTab = createBuildingTab();
//        Tab residentTab = createResidentTab();
//        Tab gateTab = createGateTab();
//        Tab securityTab = createSecurityTab();
//
//        tabPane.getTabs().addAll(buildingTab, residentTab, gateTab, securityTab);
//
//        root.setCenter(tabPane);
//        return root;
//    }
//
//    private Tab createBuildingTab() {
//        Tab tab = new Tab("Buildings");
//        tab.setClosable(false);
//
//        BuildingUI buildingUI = new BuildingUI();
//        ScrollPane scrollContent = new ScrollPane(buildingUI.createContent());
//        scrollContent.setFitToWidth(true);
//        scrollContent.setFitToHeight(true);
//
//        tab.setContent(scrollContent);
//        return tab;
//    }
//
//    private Tab createResidentTab() {
//        Tab tab = new Tab("Residents");
//        tab.setClosable(false);
//
//        ResidentUI residentUI = new ResidentUI();
//        ScrollPane scrollContent = new ScrollPane(residentUI.createContent());
//        scrollContent.setFitToWidth(true);
//        scrollContent.setFitToHeight(true);
//
//        tab.setContent(scrollContent);
//        return tab;
//    }
//
//    private Tab createGateTab() {
//        Tab tab = new Tab("Gates");
//        tab.setClosable(false);
//
//        GateUI gateUI = new GateUI();
//        ScrollPane scrollContent = new ScrollPane(gateUI.createContent());
//        scrollContent.setFitToWidth(true);
//        scrollContent.setFitToHeight(true);
//
//        tab.setContent(scrollContent);
//        return tab;
//    }
//
//    private Tab createSecurityTab() {
//        Tab tab = new Tab("Security");
//        tab.setClosable(false);
//
//        SecurityPersonnelUI securityUI = new SecurityPersonnelUI();
//        ScrollPane scrollContent = new ScrollPane(securityUI.createContent());
//        scrollContent.setFitToWidth(true);
//        scrollContent.setFitToHeight(true);
//
//        tab.setContent(scrollContent);
//        return tab;
//    }
//
//    // For dynamic tab management (if needed for future extensions)
//    public void addTab(String title, javafx.scene.Node content) {
//        Tab newTab = new Tab(title);
//        newTab.setContent(content);
//        newTab.setClosable(true);
//        tabPane.getTabs().add(newTab);
//        tabPane.getSelectionModel().select(newTab);
//    }
//}

package application;


import dao.DBConnection;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.sql.SQLException;


public class UIManager {
    private TabPane tabPane;



    public BorderPane createMainLayout() {
        BorderPane root = new BorderPane();
        tabPane = new TabPane();


        // Add all tabs in logical order
        tabPane.getTabs().addAll(
            createBuildingTab(),
            createResidentTab(),
            createTenantTab(),
            createOwnerTab(),
            createParkingSlotTab(),
            createFlatTab(),
            createCleanerTab(),
            createGateTab(),
            createSecurityTab()
        );


        root.setCenter(tabPane);
        return root;
    }


    private Tab createBuildingTab() {
        Tab tab = new Tab("Buildings");
        tab.setClosable(false);
        BuildingUI buildingUI = new BuildingUI();
        ScrollPane scrollContent = new ScrollPane(buildingUI.createContent());
        scrollContent.setFitToWidth(true);
        scrollContent.setFitToHeight(true);
        tab.setContent(scrollContent);
        return tab;
    }


    private Tab createResidentTab() {
        Tab tab = new Tab("Residents");
        tab.setClosable(false);
        ResidentUI residentUI = new ResidentUI();
        ScrollPane scrollContent = new ScrollPane(residentUI.createContent());
        scrollContent.setFitToWidth(true);
        scrollContent.setFitToHeight(true);
        tab.setContent(scrollContent);
        return tab;
    }


    private Tab createTenantTab() {
        Tab tab = new Tab("Tenants");
        tab.setClosable(false);
        try {
            TenantUI tenantUI = new TenantUI(DBConnection.getConnection());
            ScrollPane scrollContent = new ScrollPane(tenantUI.createContent());
            scrollContent.setFitToWidth(true);
            scrollContent.setFitToHeight(true);
            tab.setContent(scrollContent);
        } catch (SQLException e) {
            e.printStackTrace();
            tab.setContent(new Label("Failed to load tenant data"));
        }
        return tab;
    }


    private Tab createOwnerTab() {
        Tab tab = new Tab("Owners");
        tab.setClosable(false);
        try {
            OwnerUI ownerUI = new OwnerUI(DBConnection.getConnection());
            ScrollPane scrollContent = new ScrollPane(ownerUI.createContent());
            scrollContent.setFitToWidth(true);
            scrollContent.setFitToHeight(true);
            tab.setContent(scrollContent);
        } catch (SQLException e) {
            e.printStackTrace();
            tab.setContent(new Label("Failed to load owner data"));
        }
        return tab;
    }


    private Tab createParkingSlotTab() {
        Tab tab = new Tab("Parking Slots");
        tab.setClosable(false);
        ParkingSlotUI parkingSlotUI = new ParkingSlotUI();
        ScrollPane scrollContent = new ScrollPane(parkingSlotUI.createContent());
        scrollContent.setFitToWidth(true);
        scrollContent.setFitToHeight(true);
        tab.setContent(scrollContent);
        return tab;
    }


    private Tab createFlatTab() {
        Tab tab = new Tab("Flats");
        tab.setClosable(false);
        FlatUI flatUI = new FlatUI();
        ScrollPane scrollContent = new ScrollPane(flatUI.createContent());
        scrollContent.setFitToWidth(true);
        scrollContent.setFitToHeight(true);
        tab.setContent(scrollContent);
        return tab;
    }


    private Tab createCleanerTab() {
        Tab tab = new Tab("Cleaners");
        tab.setClosable(false);
        CleanerUI cleanerUI = new CleanerUI();
        ScrollPane scrollContent = new ScrollPane(cleanerUI.createContent());
        scrollContent.setFitToWidth(true);
        scrollContent.setFitToHeight(true);
        tab.setContent(scrollContent);
        return tab;
    }


    private Tab createGateTab() {
        Tab tab = new Tab("Gates");
        tab.setClosable(false);
        GateUI gateUI = new GateUI();
        ScrollPane scrollContent = new ScrollPane(gateUI.createContent());
        scrollContent.setFitToWidth(true);
        scrollContent.setFitToHeight(true);
        tab.setContent(scrollContent);
        return tab;
    }


    private Tab createSecurityTab() {
        Tab tab = new Tab("Security");
        tab.setClosable(false);
        SecurityPersonnelUI securityUI = new SecurityPersonnelUI();
        ScrollPane scrollContent = new ScrollPane(securityUI.createContent());
        scrollContent.setFitToWidth(true);
        scrollContent.setFitToHeight(true);
        tab.setContent(scrollContent);
        return tab;
    }


    public void addTab(String title, Node content) {
        Tab newTab = new Tab(title);
        newTab.setContent(content);
        newTab.setClosable(true);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }
}











