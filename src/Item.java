public class Item {

    private String englishItemName;
    private int quality;
    private String itemID;
    private float dayQuantity;
    private float buyPrice;
    private float sellPrice;

    public Item(String englishItemName, int quality, String itemID, float dayQuantity, float buyPrice, float sellPrice) {
        this.englishItemName = englishItemName;
        this.quality = quality;
        this.itemID = itemID;
        this.dayQuantity = dayQuantity;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public Item(String englishItemName, String itemID) {
        this.englishItemName = englishItemName;
        this.itemID = itemID;
    }

    public Item(){
        this.itemID="wrongItem";
    }

    float getRentability(){
        if (buyPrice==0){
            return 0;
        }
        return (sellPrice-buyPrice)/buyPrice;
    }

    @Override
    public String toString() {
        return "Item{" +
                "englishItemName='" + englishItemName + '\'' +
                ", quality=" + quality +
                ", itemID='" + itemID + '\'' +
                ", dayQuantity=" + dayQuantity +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                '}';
    }

    public String getItemID() {
        return itemID;
    }

    public String getEnglishItemName() {
        return englishItemName;
    }

}
