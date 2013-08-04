package mods.nurseangel.orehaba.handler;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IPlayerTracker;

/**
 * プレイヤーハンドラ
 *
 */
public class OrehabaPlayerTracker implements IPlayerTracker {

	/**
	 * ログインした<br />
	 */
	@Override
	public void onPlayerLogin(EntityPlayer player) {
		// 世界を更新
		HandlerController.getInstance().updateWorld(player.worldObj);
	}

	/**
	 * ログアウトした<br />
	 */
	@Override
	public void onPlayerLogout(EntityPlayer player) {
		HandlerController.getInstance().effectiveHandler(false);
	}

	/**
	 * ディメンションを移動した
	 */
	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		HandlerController.getInstance().updateWorld(player.worldObj);
	}

	/**
	 * リスポーンした
	 */
	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		// リスポン地点が範囲外かもしれないため、 一時的に無効化する
		HandlerController.getInstance().effectiveHandler(false);
	}

}
