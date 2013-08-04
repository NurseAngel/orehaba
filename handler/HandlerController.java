package mods.nurseangel.orehaba.handler;

import mods.nurseangel.orehaba.Config;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * ハンドラコントローラクラス<br />
 * ハンドラ間の受け渡しなどを行う。<br />
 * たぶんここにメソッド書かずに直接呼べばいい気がした
 *
 */
public class HandlerController {

	private static HandlerController instance = new HandlerController();
	private Config config;
	private OrehabaTickHandler orehabaTickHandler;
	private OrehabaPlayerTracker orehabaPlayerTracker;
	private OrehabaPlayerEtcHandler orehabaPlayerEtcHandler;

	/**
	 * シングルトン
	 */
	private HandlerController() {
	}

	public static HandlerController getInstance() {
		return instance;
	}

	public HandlerController setConfig(Config config) {
		this.config = config;
		return instance;
	}

	/**
	 * ハンドラ登録
	 *
	 * @param side
	 *            Side.CLIENT/SERVER
	 */
	public void registerHandler(Side side) {

		// tickハンドラ
		orehabaTickHandler = new OrehabaTickHandler(config);
		TickRegistry.registerScheduledTickHandler(orehabaTickHandler, side);

		// プレイヤーハンドラ
		orehabaPlayerTracker = new OrehabaPlayerTracker();
		GameRegistry.registerPlayerTracker(orehabaPlayerTracker);

		// ↑で足りないハンドラ
		orehabaPlayerEtcHandler = new OrehabaPlayerEtcHandler();
		MinecraftForge.EVENT_BUS.register(orehabaPlayerEtcHandler);
	}

	/**
	 * ハンドラ削除
	 *
	 * @param side
	 *            Side.CLIENT/SERVER
	 */
	public void unregisterHandler(Side side) {

		/**
		 * TODO unregisterが無い?
		 */
	}

	/**
	 * ディメンション移動などでWorldを更新した
	 *
	 * @param world
	 * @return
	 */
	public HandlerController updateWorld(World world) {
		orehabaTickHandler.updateWorld(world);
		return instance;
	}

	/**
	 * 俺幅ハンドラを一時的に有効化/無効化
	 *
	 * @param b
	 *            有効にするならtrue
	 */
	public void effectiveHandler(boolean b) {
		orehabaTickHandler.enableTickHandler(b);

	}

	/**
	 * ブロックが世界に置かれた
	 *
	 * @param par1World
	 * @param x
	 * @param y
	 * @param z
	 * @param direction
	 *            方向
	 */
	public void onBlockPlacedByPlayer(World par1World, int x, int y, int z, int direction) {
		orehabaTickHandler.onBlockPlacedByPlayer(par1World, x, y, z, direction);
	}
}
