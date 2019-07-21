/*
 * Copyright 2019 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.nui.market;

import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.logic.market.MarketClientSystem;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.layouts.FlowLayout;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.layers.ingame.inventory.ItemIcon;
import org.terasology.world.block.Block;

import java.util.Set;

public class MarketScreen extends CoreScreenLayer {

    private FlowLayout marketItemList;
    private UILabel itemName;
    private ItemIcon itemDisplayIcon;
    private UILabel itemDescription;
    private Prefab selectedPrefab;
    private Block selectedBlock;

    @In
    MarketClientSystem marketManager;

    @Override
    public void initialise() {
        marketItemList = find("marketItemList", FlowLayout.class);
        itemName = find("itemName", UILabel.class);
        itemDisplayIcon = find("itemDisplayIcon", ItemIcon.class);
        itemDescription = find("itemDescription", UILabel.class);
    }

    @Override
    public void onOpened() {
        populateMarketItems(marketManager.getPurchasablePrefabs(), marketManager.getPurchasableBlocks());
    }

    private void populateMarketItems(Set<Prefab> purchasablePrefabs, Set<Block> purchasableBlocks) {
        for (Prefab prefab : purchasablePrefabs) {
            ItemComponent itemComponent = prefab.getComponent(ItemComponent.class);
            ItemIcon itemIcon = new ItemIcon();
            itemIcon.setIcon(itemComponent.icon);

            marketItemList.addWidget(itemIcon, null);
        }
        for (Block block : purchasableBlocks) {
            ItemIcon itemIcon = new ItemIcon();
            itemIcon.setMesh(block.getMeshGenerator().getStandaloneMesh());
            marketItemList.addWidget(itemIcon, null);
        }
    }
}
