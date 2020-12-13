package e.user.diarytoday;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {
    static DataManager sDataManager;

    @BeforeClass
    public static  void classSetUp() throws Exception{
        sDataManager = DataManager.getInstance();
    }
    @Before
    public void setUp() throws Exception{

        sDataManager.getDiaries().clear();
        sDataManager.initializeExampleDiaries();
    }

    @Test
    public void createNewDiary() throws Exception{

        final SubjectInfo subject = sDataManager.getSubject("sport_life");
        final String diaryTitle = "Test diary title";
        final String diaryText = "This is the body of my text diary";

        int diaryIndex = sDataManager.createNewDiary();
        DiaryInfo newDiary = sDataManager.getDiaries().get(diaryIndex);
        newDiary.setSubject(subject);
        newDiary.setTitle(diaryTitle);
        newDiary.setText(diaryText);

        DiaryInfo compareDiary = sDataManager.getDiaries().get(diaryIndex);
        assertSame(newDiary, compareDiary);
        assertEquals(compareDiary.getSubject(), subject);
        assertEquals(compareDiary.getTitle(), diaryTitle);
        assertEquals(compareDiary.getText(), diaryText);
    }

    @Test
    public void findSimilarDiary() throws Exception{

        final SubjectInfo subject = sDataManager.getSubject("sport_life");
        final String diaryTitle = "Test diary title";
        final String diaryText1 = "This is the body text of my test diary";
        final String diaryText2 = "This is the body of my second test diary";

        int diaryIndex1 = sDataManager.createNewDiary();
        DiaryInfo newDiary1 = sDataManager.getDiaries().get(diaryIndex1);
        newDiary1.setSubject(subject);
        newDiary1.setTitle(diaryTitle);
        newDiary1.setText(diaryText1);

        int diaryIndex2 = sDataManager.createNewDiary();
        DiaryInfo newDiary2 = sDataManager.getDiaries().get(diaryIndex2);
        newDiary2.setSubject(subject);
        newDiary2.setTitle(diaryTitle);
        newDiary2.setText(diaryText2);

        int foundIndex1 = sDataManager.findDiary(newDiary1);
        assertEquals(diaryIndex1, foundIndex1);

        int foundIndex2 = sDataManager.findDiary(newDiary2);
        assertEquals(diaryIndex2, foundIndex2);
    }

    @Test
    public void createNewDiaryOneStepCreation(){
        final SubjectInfo subject = sDataManager.getSubject("sport_life");
        final String diaryTitle = "Test diary title";
        final String diaryText = "This is the body of my text diary";

        int diaryIndex = sDataManager.createNewDiary(subject, diaryTitle, diaryText);

        DiaryInfo compareDiary = sDataManager.getDiaries().get(diaryIndex);
        assertEquals(subject, compareDiary.getSubject());
        assertEquals(diaryTitle, compareDiary.getTitle());
        assertEquals(diaryText, compareDiary.getText());
    }
}