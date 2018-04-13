package ru.lorddux.distasksystem.storage.receiver.processors;

import ru.lorddux.distasksystem.storage.data.WorkerTaskResult;

public interface SentenceProcessor {
    WorkerTaskResult decode(String sentence);
}
