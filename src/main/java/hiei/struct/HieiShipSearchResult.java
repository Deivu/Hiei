package hiei.struct;

public class HieiShipSearchResult {
    public int score;
    public HieiShip shipData;

    public HieiShipSearchResult(int score, HieiShip shipData) {
        this.score = score;
        this.shipData = shipData;
    }
}
