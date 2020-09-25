package hiei.struct;

public class HieiSearchResult {
    public final int score;
    private final HieiShip ship;
    private final HieiEquip equip;

    public HieiSearchResult(int score, HieiShip ship) {
        this.score = score;
        this.ship = ship;
        this.equip = null;
    }

    public HieiSearchResult(int score, HieiEquip equip) {
        this.score = score;
        this.ship = null;
        this.equip = equip;
    }

    public HieiShip getShip() { return this.ship; }

    public HieiEquip getEquip() { return this.equip; }
}
