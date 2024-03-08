package org.albert.evernote.ai.job;

import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import java.io.IOException;
import java.time.Duration;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EverNoteSyncJobTest {

    @Autowired EverNoteSyncJob job;

    @Disabled
    @Test
    public void testRunJob()
            throws TException,
                    EDAMNotFoundException,
                    EDAMSystemException,
                    EDAMUserException,
                    TikaException,
                    IOException,
                    InterruptedException {
        while (true) {
            try {
                job.runJob();
            } catch (EDAMSystemException e) {
                if (e.getErrorCode() == EDAMErrorCode.RATE_LIMIT_REACHED) {
                    System.out.println(
                            String.format(
                                    "Rate limit reached, waiting %d seconds",
                                    e.getRateLimitDuration()));
                    Thread.sleep(Duration.ofSeconds(e.getRateLimitDuration()));
                }
            }
        }
    }
}
