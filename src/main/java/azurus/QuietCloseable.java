package azurus;

interface QuietCloseable extends AutoCloseable {
    @Override
    void close();
}
