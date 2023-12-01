package net.sf.freecol.newFeatures;

import net.sf.freecol.common.model.*;
import net.sf.freecol.server.ServerTestHelper;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.model.ServerPlayer;
import net.sf.freecol.server.model.ServerUnit;
import net.sf.freecol.util.test.FreeColTestCase;

public class WagonTest extends FreeColTestCase {

    private static final UnitType wagonTrainType
            = spec().getUnitType("model.unit.wagonTrain");

    private static final UnitType wagonTrainWithHorsesType
            = spec().getUnitType("model.unit.wagonWithHorses");

    private static final UnitType artilleryType
            = spec().getUnitType("model.unit.artillery");

    private static final UnitType braveType
            = spec().getUnitType("model.unit.brave");
    private static final UnitType caravelType
            = spec().getUnitType("model.unit.caravel");

    private static final GoodsType horsesType
            =spec().getGoodsType("model.goods.horses");

    private static final GoodsType cottonType
            = spec().getGoodsType("model.goods.cotton");

    private static final GoodsType lumberType
            = spec().getGoodsType("model.goods.lumber");

    public void testWagonType(){
        Game game = getStandardGame();
        Player english = game.getPlayerByNationId("model.nation.english");

        Unit wagonTrain = new ServerUnit(game, null, english, wagonTrainType);
        Unit wagonTrainWithHorses = new ServerUnit(game, null, english, wagonTrainWithHorsesType);
        Unit artillery = new ServerUnit(game, null, english, artilleryType);
        Unit brave = new ServerUnit(game, null, english, braveType);
        Unit caravel = new ServerUnit(game, null, english, caravelType);

        //Check Wagon Type
        assertTrue("Is a Wagon Train", wagonTrain.isWagonTrain());
        assertTrue("Is a Wagon Train", wagonTrainWithHorses.isWagonTrain());

        assertFalse("Artillery is not a type of Wagon Train", artillery.isWagonTrain());
        assertFalse("Brave is not a type of Wagon Train", brave.isWagonTrain());
        assertFalse("Caravel is not a type of Wagon Train", caravel.isWagonTrain());

        //Check if wagons can carry goods
        assertTrue("Wagon should be able to carry goods", wagonTrain.canCarryGoods());
        assertTrue("Wagon with horses should be able to carry goods", wagonTrainWithHorses.canCarryGoods());

        //Check wagons movements
        assertEquals(6, wagonTrain.getInitialMovesLeft());
        assertEquals(24, wagonTrainWithHorses.getInitialMovesLeft());

        //Check wagons initial turns left
        assertEquals(40, wagonTrain.getTurnsLeft());
    }

    public void testWagonTypeChange(){
        Game game = ServerTestHelper.startServerGame(getTestMap());
        InGameController igc = ServerTestHelper.getInGameController();

        Colony colony = getStandardColony();
        colony.addGoods(horsesType, 50);
        colony.addGoods(cottonType, 50);
        ServerPlayer english = getServerPlayer(game, "model.nation.english");
        Unit wagonTrain = new ServerUnit(game, colony.getTile(), english, wagonTrainType);

        assertEquals("Wagon Train should be empty", 0, wagonTrain.getUnitSpaceTaken());

        // Change to Wagon with Horses Type
        assertEquals(wagonTrainType, wagonTrain.getType());
        assertEquals(0, wagonTrain.getGoodsCount(horsesType));
        igc.loadGoods(english, colony, horsesType, 50, wagonTrain);
        assertEquals(50, wagonTrain.getGoodsCount(horsesType));
        assertEquals(wagonTrainWithHorsesType, wagonTrain.getType());

        // Change to Wagon Train Type
        igc.unloadGoods(english,horsesType,50,wagonTrain);
        assertEquals("Should be zero", 0, wagonTrain.getGoodsCount(horsesType));
        assertEquals(wagonTrainType, wagonTrain.getType());

        assertEquals("Should be empty", 0, wagonTrain.getGoodsSpaceTaken());

        // Shouldn't change because the goods loaded aren't horses
        igc.loadGoods(english, colony, cottonType, 50, wagonTrain);
        assertEquals(50, wagonTrain.getGoodsCount(cottonType));
        assertEquals(wagonTrainType, wagonTrain.getType());
    }

    // Case when we load two types of goods that aren't horses
    public void testLoseGoods(){
        Game game = ServerTestHelper.startServerGame(getTestMap());
        ServerPlayer english = getServerPlayer(game, "model.nation.english");
        Unit wagonTrain = new ServerUnit(game, null, english, wagonTrainType);

        // At the beginning, the wagon must have 2 holds and 40 turns left
        assertEquals(40, wagonTrain.getTurnsLeft());
        assertEquals("Before any load",2, wagonTrain.getCargoCapacity());

        // Load goods
        wagonTrain.addGoods(cottonType,50);
        assertEquals("Loaded cotton",1, wagonTrain.getSpaceLeft());
        wagonTrain.addGoods(lumberType,50);
        assertEquals("Loaded lumber",0, wagonTrain.getSpaceLeft());

        // At half of max turns left, the wagon must lose a hold and the goods on it
        wagonTrain.setTurnsLeft(20);
        assertEquals(20, wagonTrain.getTurnsLeft());
        game.setCurrentPlayer(english);
        ServerTestHelper.getInGameController().endTurn(english);
        assertEquals(19, wagonTrain.getTurnsLeft());

        assertEquals(1, wagonTrain.getCargoCapacity());
        assertEquals("Should lose cotton",0, wagonTrain.getGoodsCount(cottonType));
        assertEquals("Shouldn't lose lumber", 50, wagonTrain.getGoodsCount(lumberType));
    }


    // Case when we load only horses to both holds
    public void testWagonLosesHorses(){
        Game game = ServerTestHelper.startServerGame(getTestMap());
        ServerPlayer english = getServerPlayer(game, "model.nation.english");
        InGameController igc = ServerTestHelper.getInGameController();
        Colony colony = getStandardColony();
        colony.addGoods(horsesType, 150);
        Unit wagonTrain = new ServerUnit(game, colony.getTile(), english, wagonTrainType);

        // At the beginning, the wagon must have 2 holds and 40 turns left
        assertEquals(40, wagonTrain.getTurnsLeft());
        assertEquals("Before any load",2, wagonTrain.getCargoCapacity());

        // Change to Wagon Train with Horses Type
        igc.loadGoods(english,colony,horsesType,150,wagonTrain);
        assertEquals("Loaded horses",0, wagonTrain.getSpaceLeft());
        assertEquals(wagonTrainWithHorsesType, wagonTrain.getType());

        // At half of max turns left, the wagon must lose a hold and the goods on it
        wagonTrain.setTurnsLeft(20);
        assertEquals(20, wagonTrain.getTurnsLeft());
        game.setCurrentPlayer(english);
        ServerTestHelper.getInGameController().endTurn(english);
        assertEquals(19, wagonTrain.getTurnsLeft());
        assertEquals(1, wagonTrain.getCargoCapacity());
        assertEquals("Should lose 50 horses",100, wagonTrain.getGoodsCount(horsesType));
    }

    // Case we load horses and another type of good into both holds
    public void testNotLoseHorses(){
        Game game = ServerTestHelper.startServerGame(getTestMap());
        ServerPlayer english = getServerPlayer(game, "model.nation.english");
        InGameController igc = ServerTestHelper.getInGameController();
        Colony colony = getStandardColony();
        colony.addGoods(horsesType, 50);
        colony.addGoods(cottonType, 50);
        Unit wagonTrain = new ServerUnit(game, colony.getTile(), english, wagonTrainType);

        // At the beginning, the wagon must have 2 holds and 40 turns left
        assertEquals(40, wagonTrain.getTurnsLeft());
        assertEquals("Before any load",2, wagonTrain.getCargoCapacity());

        // Load goods
        igc.loadGoods(english,colony,horsesType,50,wagonTrain);
        assertEquals("Loaded horses",1, wagonTrain.getSpaceLeft());
        igc.loadGoods(english,colony,cottonType,50,wagonTrain);
        assertEquals("Loaded cotton",0, wagonTrain.getSpaceLeft());

        // At half of max turns left, the wagon must lose a hold and the goods on it
        wagonTrain.setTurnsLeft(20);
        assertEquals(20, wagonTrain.getTurnsLeft());
        game.setCurrentPlayer(english);
        ServerTestHelper.getInGameController().endTurn(english);
        assertEquals(19, wagonTrain.getTurnsLeft());
        assertEquals(1, wagonTrain.getCargoCapacity());
        assertEquals("Should lose cotton",0, wagonTrain.getGoodsCount(cottonType));
        assertEquals("Shouldn't lose horses",50, wagonTrain.getGoodsCount(horsesType));
    }

    public void testLostWagon(){
        Game game = ServerTestHelper.startServerGame(getTestMap());
        ServerPlayer english = getServerPlayer(game, "model.nation.english");
        Unit wagonTrain = new ServerUnit(game, null, english, wagonTrainType);
        Unit wagonTrainWithHorses = new ServerUnit(game, null, english, wagonTrainWithHorsesType);

        // The wagon should be disposed when it reaches zero turns left.
        wagonTrain.setTurnsLeft(0);
        wagonTrainWithHorses.setTurnsLeft(0);
        game.setCurrentPlayer(english);
        ServerTestHelper.getInGameController().endTurn(english);
        assertTrue(wagonTrain.isDisposed());
        assertTrue(wagonTrainWithHorses.isDisposed());
    }

}
