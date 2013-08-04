package mods.nurseangel.orehaba.handler;

import mods.nurseangel.orehaba.OrehabaUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.FillBucketEvent;

/**
 * ブロックを破壊したときに呼ばれるイベントがほしいのだが今のところ無いようだ。<br />
 * PlayerEvent.HarvestCheckは壊せないブロックを呼んだときの模様
 */
public class OrehabaPlayerEtcHandler {

	/**
	 * バケツを掬ったイベント
	 *
	 * @param event
	 */
	@ForgeSubscribe
	public void etcFillBucketEvent(FillBucketEvent event) {

		// 手持ちが空バケツ以外はそのまま
		ItemStack nowItem = event.entityPlayer.getCurrentEquippedItem();
		if (nowItem.itemID != Item.bucketEmpty.itemID) {
			return;
		}

		// 現在ハンドラが有効でなければそのまま
		if (!OrehabaTickHandler.isInEffective()) {
			return;
		}

		// 掬おうとしてる対象ブロックが範囲内かチェック
		MovingObjectPosition target = event.target;
		if (OrehabaUtils.isInArea(event.world, target.blockX + 0.5, target.blockY, target.blockZ + 0.5, OrehabaTickHandler.blockX, 0,
				OrehabaTickHandler.blockZ, OrehabaTickHandler.direction)) {
			return;
		}

		// 範囲外であれば掬えない。キャンセル
		if (event.isCancelable()) {
			event.setCanceled(true);
		}
	}

	/**
	 * TODO 実装予定<br />
	 * 現在ハンドラがないので実装できない
	 *
	 * ・ブロックを破壊したイベント<br />
	 * 範囲外であれば、葉などの破壊許可ブロック以外は破壊不可
	 *
	 * ・スティッキーピストンのイベント<br />
	 * 範囲外からは引き寄せられなくする
	 *
	 */

}
