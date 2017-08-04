package abc;

import java.io.IOException;

import org.xmind.core.Core;
import org.xmind.core.CoreException;
import org.xmind.core.ISheet;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;
import org.xmind.core.IWorkbookBuilder;

public class Main {

    public static void main(String[] args) {
        String newWorkbookPath = "C:/Users/fan_x/Desktop/new_office.xmind";
        String oldWorkbookPath = "C:/Users/fan_x/Desktop/office.xmind";

        IWorkbookBuilder builder = Core.getWorkbookBuilder();
        IWorkbook workbook = builder.createWorkbook(newWorkbookPath);
        IWorkbook oldWorkbook = null;
        try {
            oldWorkbook = builder.loadFromPath(oldWorkbookPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        workbook.createTopic();

        ISheet defSheet = workbook.getPrimarySheet();
        ITopic rootTopic = defSheet.getRootTopic();
        rootTopic.setTitleText("Root");

        ITopic topic1 = workbook.createTopic();
        ITopic topic2 = workbook.createTopic();
        ITopic topic3 = workbook.createTopic();
        topic1.setTitleText("topic1");
        topic2.setTitleText("topic2");
        topic3.setTitleText("topic4");

        rootTopic.add(topic1);
        rootTopic.add(topic2);
        topic2.add(topic3);

        try {
            workbook.save(newWorkbookPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(oldWorkbook.toString());
    }

}
