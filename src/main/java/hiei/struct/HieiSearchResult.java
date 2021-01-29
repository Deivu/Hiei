package hiei.struct;

import hiei.HieiServer;

public class HieiSearchResult {
    private final HieiServer hiei;
    private final HieiShip ship;
    private final HieiEquip equip;
    private final HieiBarrage barrage;
    private final HieiEvent event;
    private final HieiKeyChapter keyChapter;
    private final HieiSubChapter subChapter;
    private final String searchName;
    public double score;

    public HieiSearchResult(HieiServer hiei, HieiShip ship) {
        this.hiei = hiei;
        this.score = 10;
        this.ship = ship;
        this.equip = null;
        this.barrage = null;
        this.event = null;
        this.keyChapter = null;
        this.subChapter = null;
        this.searchName = ship.name;
    }

    public HieiSearchResult(HieiServer hiei, HieiEquip equip) {
        this.hiei = hiei;
        this.score = 10;
        this.ship = null;
        this.equip = equip;
        this.barrage = null;
        this.event = null;
        this.keyChapter = null;
        this.subChapter = null;
        this.searchName = equip.name;
    }

    public HieiSearchResult(HieiServer hiei, HieiBarrage barrage) {
        this.hiei = hiei;
        this.score = 10;
        this.ship = null;
        this.equip = null;
        this.barrage = barrage;
        this.event = null;
        this.keyChapter = null;
        this.subChapter = null;
        this.searchName = barrage.name;
    }

    public HieiSearchResult(HieiServer hiei, HieiBarrage barrage, String ship) {
        this.hiei = hiei;
        this.score = 10;
        this.ship = null;
        this.equip = null;
        this.barrage = barrage;
        this.event = null;
        this.keyChapter = null;
        this.subChapter = null;
        this.searchName = ship;
    }

    public HieiSearchResult(HieiServer hiei, HieiEvent event) {
        this.hiei = hiei;
        this.score = 10;
        this.ship = null;
        this.equip = null;
        this.barrage = null;
        this.event = event;
        this.keyChapter = null;
        this.subChapter = null;
        this.searchName = event.name;
    }

    public HieiSearchResult(HieiServer hiei, HieiKeyChapter  keyChapter) {
        this.hiei = hiei;
        this.score = 10;
        this.ship = null;
        this.equip = null;
        this.barrage = null;
        this.event = null;
        this.keyChapter = keyChapter;
        this.subChapter = null;
        this.searchName = keyChapter.name;
    }

    public HieiSearchResult(HieiServer hiei, HieiSubChapter subChapter) {
        this.hiei = hiei;
        this.score = 10;
        this.ship = null;
        this.equip = null;
        this.barrage = null;
        this.event = null;
        this.keyChapter = null;
        this.subChapter = subChapter;
        this.searchName = subChapter.name;
    }

    public HieiSearchResult analyzeScore(String comparator) {
        this.score = this.hiei.levenshteinDistance.getDistance(this.searchName, comparator);
        return this;
    }

    public HieiShip getShip() { return this.ship; }

    public HieiEquip getEquip() { return this.equip; }

    public HieiBarrage getBarrage() { return this.barrage; }

    public HieiEvent getEvent() { return this.event; }

    public HieiKeyChapter getKeyChapter() { return this.keyChapter; }

    public HieiSubChapter getSubChapter() { return this.subChapter; }
}
