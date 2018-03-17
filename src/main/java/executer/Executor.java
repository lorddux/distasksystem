package executer;

public interface Executor extends Runnable {
    void loadArtifact(String url) throws Exception;
}
