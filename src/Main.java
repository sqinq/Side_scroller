import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        Model model = new Model();
        //EditorModel editorM = new EditorModel();
        MainView mainView = new MainView(model);
    }
}
