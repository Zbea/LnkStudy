package com.bll.lnkstudy.utils

import com.bll.lnkstudy.mvp.model.paper.ScoreItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.regex.Pattern

object ScoreItemUtils {

    fun getAIJsonScore(message: String): String {
        val pattern = Pattern.compile("```json\\n(.*?)\\n```", Pattern.DOTALL)
        val matcher = pattern.matcher(message)
        if (matcher.find()) {
            return matcher.group(1)
        }
        return ""
    }

    fun updateAIJsonScores(currentScores: List<ScoreItem>, updateList: List<ScoreItem>) {
        var i = 0
        // 递归更新函数
        fun updateNode(currentItem:  ScoreItem,parentItem: ScoreItem?) {
            if (i >= updateList.size) return
            // 优先检查当前节点是否匹配
            if (currentItem.label == updateList[i].label) {
                currentItem.score = updateList[i].score
                currentItem.result = updateList[i].result
                i += 1

                //将子节点值赋值给父节点
                if (parentItem!=null){
                    parentItem.score+=currentItem.score
                    parentItem.result=if (parentItem.score<parentItem.label) 0 else 1
                }
                return  // 匹配成功后终止当前分支的进一步检查
            }
            // 递归处理子节点（深度优先）
            currentItem.childScores?.forEach { child ->
                updateNode(child,currentItem)
            }
        }

        // 遍历根节点
        currentScores.forEach { rootNode ->
            updateNode(rootNode,null)
        }
    }

    /**
     * 格式序列化  题目分数转行list集合
     */
    fun questionToList(json: String,correctModule: Int): MutableList<ScoreItem> {
        if (json.isEmpty()){
            return mutableListOf()
        }
        val list=Gson().fromJson(json, object : TypeToken<MutableList<ScoreItem>>() {}.type) as MutableList<ScoreItem>
        if (correctModule==4||correctModule==7){
            var sort=0
            for (item in list){
                item.childScores.forEach {
                    it.sort=sort
                    sort+=1
                }
            }
        }

        setInitBaseInfo(list,null)
        setInitListScore(list)
        return list
    }

    /**
     * 题目分数多级树列表转成模板级数
     */
    fun jsonListToModuleList(json: String,correctModule: Int): MutableList<ScoreItem> {
        val list= questionToList(json,correctModule)
        val items = mutableListOf<ScoreItem>()
        when (correctModule) {
            1,2 -> {
                for (item in list) {
                    item.sortStr=if (correctModule==1)ToolUtils.numbers[item.sort+1] else "${item.sort+1}"
                    items.add(item)
                }
            }
            3, 4 -> {
                for (item in list) {
                    item.sortStr=ToolUtils.numbers[item.sort+1]
                    item.childScores.forEach {
                        it.sortStr=" ${it.sort+1}"
                    }
                    items.add(item)
                }
            }
            5 -> {
                for (item in list) {
                    item.sortStr="${item.sort+1}"
                    //处理当前级数据如果有第3级则显示sortStr
                    if (isListExistChildItem(item.childScores)){
                        val childItems = mutableListOf<ScoreItem>()
                        item.childScores.forEach {
                            if (it.childScores.isNullOrEmpty()){
                                it.sortStr=" (${it.sort+1})"
                                childItems.add(it)
                            }
                            else{
                                //超过两级的去掉父节点拿到所有子节点
                                childItems.addAll(getRecursionChildItems(correctModule,it.childScores, it))
                            }
                        }
                        item.childScores=childItems
                    }
                    items.add(item)
                }
            }
            6,7->{
                for (item in list) {
                    item.sortStr=ToolUtils.numbers[item.sort+1]
                    val parentItems = mutableListOf<ScoreItem>()
                    for (parentItem in item.childScores) {
                        parentItem.sortStr=" ${parentItem.sort+1}"
                        //处理当前级数据如果有第4级则显示sortStr
                        if (isListExistChildItem(parentItem.childScores)){
                            val childItems = mutableListOf<ScoreItem>()
                            parentItem.childScores.forEach { childItem->
                                if (childItem.childScores.isNullOrEmpty()) {
                                    childItem.sortStr=" (${childItem.sort+1})"
                                    childItems.add(childItem)
                                } else {
                                    childItems.addAll(getRecursionChildItems(correctModule,childItem.childScores, childItem))
                                }
                            }
                            parentItem.childScores=childItems
                        }
                        parentItems.add(parentItem)
                    }
                    item.childScores=parentItems
                    items.add(item)
                }
            }
        }
        return items
    }

    /**
     * 把当前数据赋值返回给初始数据
     */
    fun updateInitListData(initList: MutableList<ScoreItem>, currentList: MutableList<ScoreItem>, correctModule: Int): MutableList<ScoreItem> {
        when (correctModule) {
            1, 2,3,4 -> {
                return currentList
            }
            else->{
                val items= getRecursionItems(currentList)
                setRecursionListAssignScore(initList,items)
                setInitListScore(initList)
            }
        }
        return initList
    }

    private fun setInitBaseInfo(list:List<ScoreItem>, parentItem: ScoreItem?){
        for (item in list){
            if (parentItem==null){
                item.level=0
                item.parentSort=0
                item.rootSort=item.sort
            }
            else{
                item.level=parentItem.level+1
                item.parentSort=parentItem.sort
                item.rootSort=parentItem.rootSort
            }
            if (!item.childScores.isNullOrEmpty()){
                setInitBaseInfo(item.childScores,item)
            }
        }
    }

    /**
     * 给数据节点赋分、以及统计对错
     */
    private fun setInitListScore(list:List<ScoreItem>){
        list.forEach { item ->
            // 1. 处理子节点（递归：先确保子节点的result已计算，避免父节点依赖未初始化的子节点数据）
            if (!item.childScores.isNullOrEmpty()) {
                setInitListScore(item.childScores) // 递归处理子节点，保证子节点result/score/label已就绪
            }
            // 2. 处理当前节点的result（子节点/父节点通用）
            item.result = getItemScoreResult(item)
            // 3. 父节点逻辑：仅当score/label为空时，才通过子节点汇总赋值（核心优化点）
            if (item.childScores.isNullOrEmpty()) {
                return@forEach
            }
            // 父节点：判断score/label是否为空
            if (item.score == 0.0) {
                item.score = calculateParentTotalScore(item.childScores)
            }
            if (item.label == 0.0) {
                item.label = calculateParentTotalLabel(item.childScores)
            }
        }
    }

    /**
     * 计算父节点总score（子节点score汇总）
     */
    private fun calculateParentTotalScore(childList: MutableList<ScoreItem>): Double {
        return childList.sumOf { child ->
            child.score
        }
    }

    /**
     * 轮询赋值父节点总label
     */
    private fun calculateParentTotalLabel(childList: MutableList<ScoreItem>): Double {
        return childList.sumOf { child ->
            child.label
        }
    }

    /**
     * 判断当前层级是否有子集
     */
    private fun isListExistChildItem(list: MutableList<ScoreItem>):Boolean{
        var isShowSortStr=false
        for (childItem in list){
            if (!childItem.childScores.isNullOrEmpty()){
                isShowSortStr=true
            }
        }
        return isShowSortStr
    }

    /**
     * 遍历全部数据
     */
    private fun getRecursionItems(list: MutableList<ScoreItem>):MutableList<ScoreItem>{
        val items = mutableListOf<ScoreItem>()
        for (item in list) {
            if (item.childScores.isNullOrEmpty()) {
                items.add(item)
            } else {
                items.addAll(getRecursionItems(item.childScores))
            }
        }
        return items
    }

    /**
     * 递归拿到所有子节点，同时给超出子节点的第一个sortStr赋值
     */
    private fun getRecursionChildItems(correctModule: Int, list: MutableList<ScoreItem>, parentItem: ScoreItem): MutableList<ScoreItem> {
        val items = mutableListOf<ScoreItem>()
        for (item in list) {
            item.sortStr=if (list.indexOf(item)==0&&item.level==if (correctModule==5)2 else 3) " (${parentItem.sort+1})" else ""
            if (item.childScores.isNullOrEmpty()) {
                items.add(item)
            } else {
                items.addAll(getRecursionChildItems(correctModule,item.childScores, item))
            }
        }
        return items
    }

    /**
     * 遍历赋分
     */
    private fun setRecursionListAssignScore(list: MutableList<ScoreItem>, scoreList: MutableList<ScoreItem>){
        for (item in list){
            item.score= getCurrentScore(item,scoreList)
            if (!item.childScores.isNullOrEmpty()){
                setRecursionListAssignScore(item.childScores,scoreList)
            }
        }
    }

    /**
     * 获取当前题目分数
     */
    private fun getCurrentScore(currentItem: ScoreItem, scoreList: MutableList<ScoreItem>): Double {
        for (item in scoreList) {
            if (item.level == currentItem.level && item.rootSort == currentItem.rootSort &&item.parentSort == currentItem.parentSort&& item.sort == currentItem.sort) {
                return item.score
            }
        }
        return 0.0
    }

    /**
     * 获取小题总分
     */
    fun getItemScoreTotal(list:List<ScoreItem>):Double{
        var scoreTotal=0.0
        for (item in list){
            scoreTotal+=item.score
        }
        return scoreTotal
    }

    /**
     * 获取小题结果
     */
    fun getItemScoreResult(item:ScoreItem):Int{
        if (item.label==0.0){
            return 0
        }
        return if (item.score<item.label) 0 else 1
    }
}