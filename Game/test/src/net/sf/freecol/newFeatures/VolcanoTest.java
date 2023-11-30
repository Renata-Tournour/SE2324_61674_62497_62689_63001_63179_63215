package net.sf.freecol.newFeatures;

import net.sf.freecol.common.model.*;
import net.sf.freecol.util.test.FreeColTestCase;

import java.util.List;

/**
 * Project ES - Madalena Pl'acido (63001) and Renata Henriques (63215)
 * Unit test class for the new extension to freecol game, a volcano
 */
public class VolcanoTest extends FreeColTestCase {

    private static final GoodsType ore
            = spec().getGoodsType("model.goods.ore");
    private static final GoodsType silver
            = spec().getGoodsType("model.goods.silver");
    private static final GoodsType grain
            = spec().getGoodsType("model.goods.grain");

    private static final GoodsType food
            = spec().getPrimaryFoodType();
    private static final ResourceType mineralsResource
            = spec().getResourceType("model.resource.minerals");
    private static final ResourceType silverResource
            = spec().getResourceType("model.resource.silver");

    private static final TileType mountains
            = spec().getTileType("model.tile.mountains");
    private static final TileType volcano
            = spec().getTileType("model.tile.volcano");

    private static final UnitType colonistType
            = spec().getUnitType("model.unit.freeColonist");

    /**
     * tests the production aspect of volcanos/mountains as well as the change of tile from volcano to mountains
     */
    public void testVolcanoTile() {
        Game game = getGame();

        // create a map with only volcanos
        Map map = getTestMap(volcano);
        game.changeMap(map);

        // get the volcano tiles
        List<Tile> volcanoTiles = map.getTileList(t -> t.getType().equals(volcano));

        Tile testTile = volcanoTiles.get(0);
        assertEquals(testTile.getType(), volcano);

        // volcanos produce ore if the tile is attended and not
        assertTrue(testTile.canProduce(ore, null));
        assertTrue(testTile.canProduce(ore, colonistType));

        // volcanos produce ore if the tile is attended and not
        assertFalse(testTile.canProduce(silver, null));
        assertTrue(testTile.canProduce(silver, colonistType));

        // volcanos cannot produce grain
        assertFalse(testTile.canProduce(grain, null));
        assertFalse(testTile.canProduce(grain, colonistType));

        // change volcano to mountain
        testTile.changeType(mountains);
        assertEquals(testTile.getType(), mountains);

        // mountains can only produce ore if the tile is attended
        assertFalse(testTile.canProduce(ore, null));
        assertTrue(testTile.canProduce(ore, colonistType));

        // mountains can only produce silver if the tile is attended
        assertFalse(testTile.canProduce(silver, null));
        assertTrue(testTile.canProduce(silver, colonistType));

        // mountains cannot produce grain
        assertFalse(testTile.canProduce(grain, null));
        assertFalse(testTile.canProduce(grain, colonistType));
    }

    /**
     * Test the production potential values for a volcano and a mountain
     * */
    public void testPotential() {

        Game game = getGame();
        // create a map with only volcanos
        Map map = getTestMap(volcano);
        game.changeMap(map);

        assertNotNull(mountains);
        assertNotNull(volcano);

        // get the volcano tiles
        List<Tile> volcanoTiles = map.getTileList(t -> t.getType().equals(volcano));

        Tile testTile = volcanoTiles.get(0);
        assertEquals(testTile.getType(), volcano);

        //Potencial silver production Volcanos if the tile is attended and not
        assertEquals(0, testTile.getPotentialProduction(silver, null));
        assertEquals(5, testTile.getPotentialProduction(silver, colonistType));

        //Potencial ore production Volcanos if the tile is attended and not
        assertEquals(4, testTile.getPotentialProduction(ore, null));
        assertEquals(8, testTile.getPotentialProduction(ore, colonistType));

        //Potencial food production Volcanos if the tile is attended and not
        assertEquals(0, testTile.getPotentialProduction(food, null));
        assertEquals(0, testTile.getPotentialProduction(food, colonistType));

        // change volcano to mountain
        testTile.changeType(mountains);
        assertEquals(testTile.getType(), mountains);

        //Potencial silver production of Mountains if the tile is attended and not
        assertEquals(0, testTile.getPotentialProduction(silver, null));
        assertEquals(1, testTile.getPotentialProduction(silver, colonistType));

        //Potencial ore production of Mountains if the tile is attended and not
        assertEquals(0, testTile.getPotentialProduction(ore, null));
        assertEquals(4, testTile.getPotentialProduction(ore, colonistType));

        //Potencial food production of Mountains if the tile is attended and not
        assertEquals(0, testTile.getPotentialProduction(food, null));
        assertEquals(0, testTile.getPotentialProduction(food, colonistType));

    }

}
