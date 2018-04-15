package ru.lorddux.distasksystem.storage.receiver.processors;

import com.google.gson.Gson;
import ru.lorddux.distasksystem.storage.data.WorkerTaskResult;

public class JsonSentenceProcessor implements SentenceProcessor {
    private Gson gson;

    public JsonSentenceProcessor() {
        gson = new Gson();
    }
    @Override
    public WorkerTaskResult decode(String sentence) {
        return gson.fromJson(sentence, WorkerTaskResult.class);
    }
}
