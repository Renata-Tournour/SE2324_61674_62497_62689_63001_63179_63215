package net.sf.freecol.newFeatures;

import net.sf.freecol.common.model.*;
import net.sf.freecol.common.util.RandomChoice;
import net.sf.freecol.server.ServerTestHelper;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.model.ServerColony;
import net.sf.freecol.server.model.ServerPlayer;
import net.sf.freecol.server.model.ServerUnit;
import net.sf.freecol.util.test.FreeColTestCase;

import java.util.List;
import java.util.Map;

/**
 * Project ES - Joana Monteiro (62689) and Catarina Costa (62497)
 * Unit test class for the new disaster Winter Plague in the freecol game
 */
public class WinterPlagueTest extends FreeColTestCase {

    public static final TileType arctic
            = spec().getTileType("model.tile.arctic");
    public static final Disaster winterPlague
            = spec().getDisaster("model.disaster.winterPlague");

    private static final GoodsType grainType
            = spec().getGoodsType("model.goods.grain");

    public void testWinterPlagueEffect(){
        Game game = ServerTestHelper.startServerGame(getTestMap());
        InGameController igc = ServerTestHelper.getInGameController();
        Map map = game.getMap();
        Tile tileOfColony = map.getTile(6, 8);
        ServerPlayer english = getServerPlayer(game, "model.nation.english");
        ServerColony colony = new ServerColony(game, english, "New Amsterdam", tileOfColony);

        igc.debugApplyDisaster(colony, winterPlague);
        List<ColonyTile> tilesAfter = colony.getColonyTiles();
        for(ColonyTile t: tilesAfter){
            assertEquals(arctic,t.getTile().getType());
        }
    }

    public void testDisaster(){
        assertNotNull(winterPlague);
        assertFalse(winterPlague.getEffects().isEmpty());
        assertTrue(winterPlague.getEffects().size() == 1);
        for (RandomChoice<Effect> choice : winterPlague.getEffects()) {
            assertNotNull(choice.getObject().getId());
            assertTrue(choice.getObject().getId().equals("model.disaster.effect.winterPlagueEffect"));
            assertTrue(choice.getProbability() > 0);
            assertTrue(choice.getObject().getProbability() > 0);
            assertEquals(choice.getProbability(), choice.getObject().getProbability());
        }
    }

    public void testProductionAfterPlague(){
        Game game = ServerTestHelper.startServerGame(getTestMap());
        InGameController igc = ServerTestHelper.getInGameController();
        Map map = game.getMap();
        Tile tileOfColony = map.getTile(6, 8);
        ServerPlayer english = getServerPlayer(game, "model.nation.english");
        ServerColony colony = new ServerColony(game, english, "New Amsterdam", tileOfColony);

        igc.debugApplyDisaster(colony, winterPlague);
        List<ColonyTile> tilesAfter = colony.getColonyTiles();
        for(ColonyTile t: tilesAfter){
            assertEquals(0, t.getBaseProduction(null, grainType, null));
        }
    }
}