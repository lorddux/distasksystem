import org.junit.Assert;
import org.junit.Test;
import ru.lorddux.distasksystem.worker.utils.PathManager;

import java.io.File;

public class PathManagerTest {
    @Test
    public void getDefaultClassPath() throws Exception {
        Assert.assertEquals(
                String.format("%s%s%s",PathManager.getSubfolder(), File.separatorChar, PathManager.class.getCanonicalName()),
                PathManager.getDefaultClassPath(PathManager.class)
        );
    }

}