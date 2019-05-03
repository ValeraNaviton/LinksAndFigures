package Assignment1;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import Assignment1.map.MapAreaSkeleton;
import Assignment1.map.ToolStateSkeleton;
import Assignment1.map.Tools;


public class MapMakerSkeleton extends Application {


    public static final String REGEX_DECIMAL = "-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?";
    private static final String REGEX_POSITIVE_INTEGER = "([1-9][0-9]*)";

    /**
     * <p>
     * this object will be used to check text against given regex.</br>
     * </p>
     */
    public static final Pattern P = Pattern.compile(REGEX_POSITIVE_INTEGER);

    /**
     * <p>
     * these static final fields are file and directory paths for this application.</br>
     * </p>
     */
    private static final String MAPS_DIRECTORY = "Assignment1/resources/maps";
    public static final String INFO_PATH = "Assignment1/resources/icons/info.txt";
    public static final String HELP_PATH = "Assignment1/resources/icons/help.txt";
    private static final String CREDITS_PATH = "Assignment1/resources/icons/credits.txt";

    private MapAreaSkeleton map;
    private ToolStateSkeleton state = ToolStateSkeleton.state();

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane();

        MenuBar menuBar = new MenuBar(
                new Menu("File", null,
                        createMenuItemAndIcon("New", (e) -> map.clearMap()),
                        createMenuItemAndIcon("Open", (e) -> loadMap(primaryStage)),
                        createMenuItemAndIcon("Save", (e) ->  saveMap(primaryStage)),
                        new SeparatorMenuItem(),
                        createMenuItemAndIcon("Exit", (e) -> primaryStage.hide())
                ),
                new Menu("Help", null,
                        createMenuItemAndIcon("Credit", (e) -> displayCredit()),
                        createMenuItemAndIcon("Info", (e) -> displayInfo()),
                        new SeparatorMenuItem(),
                        createMenuItemAndIcon("Help", (e) -> displayHelp())
                )
        );

        map = new MapAreaSkeleton();
        root.setLeft(createToolBar());
        root.setTop(menuBar);
        root.setCenter(map);

        Scene scene = new Scene(root, 800, 800);
        scene.getStylesheets().add(new File("resources/css/style.css").toURI().toString());
        //if escape key is pressed quit the application
        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if (e.getCode() == KeyCode.ESCAPE)
                primaryStage.hide();
        });
        primaryStage.setScene(scene);
        primaryStage.setTitle("Map Maker Skeleton");
        primaryStage.show();
    }

    private ToolBar createToolBar() {
        ToolBar toolBar = new ToolBar(
                createButtonTool("Select", e -> {

                    ToolStateSkeleton.state().setTool(Tools.SELECT);

                }),
                createButtonTool("Move", e -> {


                    ToolStateSkeleton.state().setTool(Tools.MOVE);

                }),
                createMenuButton("Room", e -> {

                    ToolStateSkeleton.state().setTool(Tools.ROOM);


                }),
                createButtonTool("Path", e -> {
                    ToolStateSkeleton.state().setTool(Tools.PATH);

                }),
                createButtonTool("Erase", e -> {

                    ToolStateSkeleton.state().setTool(Tools.ERASE);

                }),
                createButtonTool("Door", e -> {
                    ToolStateSkeleton.state().setTool(Tools.DOOR);

                }));
        toolBar.setOrientation(Orientation.VERTICAL);
        return toolBar;
    }

    Button createButtonTool(String buttonName, EventHandler<ActionEvent> e) {
        Button button = new Button("", createLabelWithId(buttonName));
        button.setId(buttonName);
        button.setPrefSize(40, 40);
        button.setOnAction(e);
        Label statusLabel = new Label(buttonName);
        return button;
    }

    private Label createLabelWithId(String labelName) {
        Label labelWithId = new Label();
        labelWithId.setId(labelName + "-icon");
        return labelWithId;
    }

    private MenuButton createMenuButton(String name, EventHandler<ActionEvent> event) {
        MenuButton menuButton = new MenuButton("", createLabelWithId(name));
        menuButton.setOnAction(event);
        menuButton.setId(name);
        menuButton.getItems().addAll(
                createMenuItem("Line", e -> {
                    ToolStateSkeleton.state().setTool(Tools.ROOM);
                    ToolStateSkeleton.state().setOption(2);

                }),
                createMenuItem("Triangle", e -> {
                    ToolStateSkeleton.state().setTool(Tools.ROOM);
                    ToolStateSkeleton.state().setOption(3);

                }),
                createMenuItem("Rectangle", e -> {
                    ToolStateSkeleton.state().setTool(Tools.ROOM);
                    ToolStateSkeleton.state().setOption(4);

                }),
                createMenuItem("Pentagon", e -> {
                    ToolStateSkeleton.state().setTool(Tools.ROOM);
                    ToolStateSkeleton.state().setOption(5);
                }),
                createMenuItem("Hexacon", e -> {
                    ToolStateSkeleton.state().setTool(Tools.ROOM);
                    ToolStateSkeleton.state().setOption(6);
                }));

        menuButton.setPrefSize(40, 40);
        menuButton.setPopupSide(Side.RIGHT);
        return menuButton;
    }

    /**
     * <p>
     * called when JavaFX application is closed or hidden.</br>
     * </p>
     */
    @Override
    public void stop() throws Exception {
        super.stop();
    }

    /**
     * <p>
     * create a {@link Button}.</br>
     * </p>
     *
     * @param id      - used as {@link Button#setId(String)} for CSS.
     *  - {@link EventHandler} object be called when {@link Button} is clicked.
     * @return created {@link Button}.
     */
    private Button createButton(String id, EventHandler<MouseEvent> event) {
        Button button = new Button();
        button.setOnMouseClicked(event);
        button.setId(id);
        return button;
    }

    /**
     * <p>
     * create a {@link MenuItem} with an icon as {@link Label}.</br>
     * </p>
     *
     * @param name    - name to be displayed on {@link MenuItem} and used as {@link MenuItem#setId(String)} for CSS.
     * @param handler - {@link EventHandler} object be called when {@link MenuItem} is clicked.
     * @return created {@link MenuItem} with an icon as {@link Label}.
     */
    private MenuItem createMenuItemAndIcon(String name, EventHandler<ActionEvent> handler) {
        Label icon = new Label();
        icon.setId(name + "-icon");
        MenuItem item = createMenuItem(name, handler);
        item.setGraphic(icon);
        return item;
    }

    /**
     * <p>
     * create a {@link MenuItem}.</br>
     * </p>
     *
     * @param name    - name to be displayed on {@link MenuItem} and used as {@link MenuItem#setId(String)} for CSS.
     * @param handler - {@link EventHandler} object be called when {@link MenuItem} is clicked.
     * @return created {@link MenuItem}.
     */
    private MenuItem createMenuItem(String name, EventHandler<ActionEvent> handler) {
        MenuItem item = new MenuItem(name);
        item.setOnAction(handler);
        item.setId(name);
        return item;
    }

    /**
     * <p>
     * load content of {@link MapMakerSkeleton#CREDITS_PATH} and display it in an {@link Alert}.</br>
     * </p>
     */
    private void displayCredit() {
        displayAlert("Credit", loadFile(CREDITS_PATH, System.lineSeparator()));
    }

    /**
     * <p>
     * display an {@link Alert} to show {@link }.</br>
     * </p>
     *
     * @param title   - string to be displayed as title of {@link Alert}
     * @param message - string content to be displayed in {@link Alert}
     */
    private void displayAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * <p>
     * read a file and convert it to one string separated with provided separator.</br>
     * </p>
     *
     * @param path      - {@link String} object containing the path to desired file.
     * @param separator - {@link String} object containing the separator
     */
    private String loadFile(String path, String separator) {
        try {
            //for each line in given file combine lines using the separator
            return Files.lines(Paths.get(path)).reduce("", (a, b) -> a + separator + b);
        } catch (IOException e) {
            e.printStackTrace();
            return "\"" + path + "\" was probably not found" + "\nmessage: " + e.getMessage();
        }
    }

    /**
     * <p>
     * ask the user where they need to save then get the content to write from
     * {@link MapAreaSkeleton#convertToString()}.</br>
     * </p>
     *
     * @param primary - {@link Stage} object that will own the {@link FileChooser}.
     */
    private void saveMap(Stage primary) {
        //get the file object to save to
        File file = getFileChooser(primary, true);
        if (file == null)
            return;
        try {
            if (!file.exists())
                file.createNewFile();
            Files.write(file.toPath(), map.convertToString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * ask the user what file they need to open then pass the content to
     * {@link MapAreaSkeleton#convertFromString(java.util.Map)}.</br>
     * </p>
     *
     * @param primary - {@link Stage} object that will own the {@link FileChooser}.
     */
    private void loadMap(Stage primary) {
        //get the file object to load from
        File file = getFileChooser(primary, false);
        if (file == null || !file.exists())
            return;
        try {
            //no parallel (threading) here but this is safer
            AtomicInteger index = new AtomicInteger(0);
            //index.getAndIncrement()/5 means every 5 elements increases by 1
            //allowing for every 5 element placed in the same key
            //for each line in file group every 5 and pass to map area
            map.convertFromString(Files.lines(file.toPath()).collect(Collectors.groupingBy(l -> index.getAndIncrement() / 5)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void displayInfo() {displayAlert("Info",  loadFile(INFO_PATH, System.lineSeparator()));}

    private void displayHelp() {displayAlert("Help", loadFile(HELP_PATH, System.lineSeparator()));}

    /**
     * <p>
     * using the {@link FileChooser} open a new window only showing .map extension;
     * in starting path of {@link MapMakerSkeleton#MAPS_DIRECTORY}.</br>
     * this function can be used to save or open file depending on the boolean argument.</br>
     * </p>
     *
     * @param primary - {@link Stage} object that will own the {@link FileChooser}.
     * @param save    - if true show {@link FileChooser#showSaveDialog(javafx.stage.Window)}
     *                else {@link FileChooser#showOpenDialog(javafx.stage.Window)}
     * @return a {@link File} representing the save or load file object
     */
    private File getFileChooser(Stage primary, boolean save) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Maps", "*.map"));
        fileChooser.setInitialDirectory(Paths.get(MAPS_DIRECTORY).toFile());
        return save ? fileChooser.showSaveDialog(primary) : fileChooser.showOpenDialog(primary);
    }

    public static void main(String[] args) {
        launch(args);
    }
}