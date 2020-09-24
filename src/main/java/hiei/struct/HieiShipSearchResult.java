package hiei.struct;

public class HieiShipSearchResult {
    public final int score;
    public final HieiShip shipData;

    public HieiShipSearchResult(int score, HieiShip shipData) {
        this.score = score;
        this.shipData = shipData;
    }
}
