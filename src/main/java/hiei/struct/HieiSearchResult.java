package hiei.struct;

public class HieiSearchResult {
    public final int score;
    private final HieiShip ship;
    private final HieiEquip equip;
    private final HieiBarrage barrage;
    private final HieiEvent event;
    private final HieiKeyChapter keyChapter;
    private final HieiSubChapter subChapter;

    public HieiSearchResult(int score, HieiShip ship) {
        this.score = score;
        this.ship = ship;
        this.equip = null;
        this.barrage = null;
        this.event = null;
        this.keyChapter = null;
        this.subChapter = null;
    }

    public HieiSearchResult(int score, HieiEquip equip) {
        this.score = score;
        this.ship = null;
        this.equip = equip;
        this.barrage = null;
        this.event = null;
        this.keyChapter = null;
        this.subChapter = null;
    }

    public HieiSearchResult(int score, HieiBarrage barrage) {
        this.score = score;
        this.ship = null;
        this.equip = null;
        this.barrage = barrage;
        this.event = null;
        this.keyChapter = null;
        this.subChapter = null;
    }

    public HieiSearchResult(int score, HieiEvent event) {
        this.score = score;
        this.ship = null;
        this.equip = null;
        this.barrage = null;
        this.event = event;
        this.keyChapter = null;
        this.subChapter = null;
    }

    public HieiSearchResult(int score, HieiKeyChapter  keyChapter) {
        this.score = score;
        this.ship = null;
        this.equip = null;
        this.barrage = null;
        this.event = null;
        this.keyChapter = keyChapter;
        this.subChapter = null;
    }

    public HieiSearchResult(int score, HieiSubChapter subChapter) {
        this.score = score;
        this.ship = null;
        this.equip = null;
        this.barrage = null;
        this.event = null;
        this.keyChapter = null;
        this.subChapter = subChapter;
    }

    public int getScore() { return this.score; }

    public HieiShip getShip() { return this.ship; }

    public HieiEquip getEquip() { return this.equip; }

    public HieiBarrage getBarrage() { return this.barrage; }

    public HieiEvent getEvent() { return this.event; }

    public HieiKeyChapter getKeyChapter() { return this.keyChapter; }

    public HieiSubChapter getSubChapter() { return this.subChapter; }
}
