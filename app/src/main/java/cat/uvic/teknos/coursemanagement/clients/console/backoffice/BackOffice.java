package cat.uvic.teknos.coursemanagement.clients.console.backoffice;

import cat.uvic.teknos.coursemanagement.clients.console.models.ModelFactory;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.RepositoryFactory;

import java.io.*;
import java.util.stream.Collectors;

import static cat.uvic.teknos.coursemanagement.clients.console.backoffice.IOUtils.*;

public class BackOffice {
    private final BufferedReader in;
    private final PrintStream out;
    private final RepositoryFactory repositoryFactory;
    private final ModelFactory modelFactory;


    public BackOffice(InputStream inputStream, OutputStream outputStream, RepositoryFactory repositoryFactory, ModelFactory modelFactory)  {
        this.in = new BufferedReader(new InputStreamReader(inputStream));
        this.out = new PrintStream(outputStream);
        this.repositoryFactory = repositoryFactory;
        this.modelFactory = modelFactory;
    }

    public void start() {
        showBanner();
        showWelcomeMessage();

        var command = "";
        do {
            showMainMenu();
            command = readLine(in);

            switch (command) {
                case "1" -> manageMusicians();
            }

        } while (!command.equals("exit"));

        out.println("Bye!");
    }

    private void manageMusicians() {
        new StudentManager(in, out, repositoryFactory, modelFactory).start();
    }


    private void showWelcomeMessage() {
        out.println("Welcome to the Course Management Back Office");
        out.println("Select a menu option or type exit to exit the application");
    }

    private void showMainMenu() {
        out.println("1. Student");
        out.println("2. Course");
        out.println("3. Genre");
    }

    private void showBanner() {
        var bannerStream = BackOffice.class.getResourceAsStream("/banner.txt");

        var banner = new BufferedReader(new InputStreamReader(bannerStream))
                .lines().collect(Collectors.joining("\n"));

        out.println(banner);
    }

}
