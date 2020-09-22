package hiei.struct;

public class HieiEquipSearchResult {
    public int score;
    public HieiEquip equipData;

    public HieiEquipSearchResult(int score, HieiEquip equipData) {
        this.score = score;
        this.equipData = equipData;
    }
}
