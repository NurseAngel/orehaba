package mods.nurseangel.orehaba.handler;

import mods.nurseangel.orehaba.OrehabaUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

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
		if (OrehabaUtils.isInAreaDirection(target.blockX + 0.5, target.blockY, target.blockZ + 0.5, OrehabaTickHandler.blockX, 0, OrehabaTickHandler.blockZ,
				OrehabaTickHandler.direction)) {
			return;
		}

		// 範囲外であれば掬えない。キャンセル
		if (event.isCancelable()) {
			event.setCanceled(true);
		}
	}

	/**
	 * 範囲外でも壊せるブロック
	 */
	public static int[] allowedBlock = { Block.leaves.blockID, Block.web.blockID, Block.web.blockID, Block.vine.blockID, Block.tallGrass.blockID,
			Block.deadBush.blockID, Block.plantYellow.blockID, Block.plantRed.blockID, Block.mushroomBrown.blockID, Block.mushroomRed.blockID,
			Block.torchWood.blockID, Block.fire.blockID, Block.crops.blockID, Block.signPost.blockID, Block.lever.blockID, Block.stoneButton.blockID,
			Block.torchRedstoneIdle.blockID, Block.torchRedstoneActive.blockID, Block.cake.blockID, Block.trapdoor.blockID, Block.carrot.blockID,
			Block.potato.blockID, Block.woodenButton.blockID };

	/**
	 * ブロックをクリックしたイベント
	 *
	 * @param event
	 *
	 */
	@ForgeSubscribe
	public void etcPlayerInteractEvent(PlayerInteractEvent event) {

		// 現在ハンドラが有効でなければそのまま
		if (!OrehabaTickHandler.isInEffective()) {
			return;
		}

		// RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK, LEFT_CLICK_BLOCKの3種類。<br />
		// 左クリック以外は不要
		if (event.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
			return;
		}

		World world = event.entityPlayer.worldObj;

		/**
		 * クリック先が範囲内であれば掘ってもよいとする<br />
		 * isInAreaDirectionではなくisInAreaにしないと、自由スペースが整地できない。
		 */
		if (OrehabaUtils.isInArea(world, event.x + 0.5, event.y, event.z + 0.5, OrehabaTickHandler.blockX, 0, OrehabaTickHandler.blockZ,
				OrehabaTickHandler.direction)) {
			return;
		}
		// 範囲外でも特定のブロックであればなにもしない
		int blockId = world.getBlockId(event.x, event.y, event.z);
		for (int i = 0; i < allowedBlock.length; i++) {
			if (allowedBlock[i] == blockId) {
				return;
			}
		}

		// キャンセル
		if (event.isCancelable()) {
			event.setCanceled(true);
		}
		/**
		 * TODO このキャンセル、"壊したけど元に戻る"という見た目になる。<br />
		 * 岩盤みたいに単に壊せなくする方法があればそうしたい。
		 */
	}

	/**
	 * TODO 実装したい<br />
	 *
	 * ・ピストンのイベント<br />
	 * 範囲外からは引き寄せられなくする
	 *
	 */

}
