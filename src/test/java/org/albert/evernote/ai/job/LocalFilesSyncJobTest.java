package org.albert.evernote.ai.job;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LocalFilesSyncJobTest {

    @Autowired LocalFilesSyncJob job;

    @Disabled
    @Test
    public void testRunJob() {
        job.runJob();
    }
}
