public enum Level {
    EASY(8, 8, 10),
    MEDIUM(16, 16, 30),
    HARD(16, 16, 50);
    
    
    int rows;
    int cols;
    int mines;
    
    Level(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
    }
}
