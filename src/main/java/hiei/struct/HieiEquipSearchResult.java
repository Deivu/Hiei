package hiei.struct;

public class HieiEquipSearchResult {
    public final int score;
    public final HieiEquip equipData;

    public HieiEquipSearchResult(int score, HieiEquip equipData) {
        this.score = score;
        this.equipData = equipData;
    }
}
