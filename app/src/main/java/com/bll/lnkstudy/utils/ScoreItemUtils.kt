package com.bll.lnkstudy.utils

import com.bll.lnkstudy.mvp.model.paper.ScoreItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ScoreItemUtils {

    /**
     * 格式序列化  题目分数转行list集合
     */
    fun questionToList(json: String,isShowResult:Boolean): MutableList<ScoreItem> {
        val list=Gson().fromJson(json, object : TypeToken<MutableList<ScoreItem>>() {}.type) as MutableList<ScoreItem>
        setInitList(list,0,isShowResult)
        return list
    }

    /**
     * 给数据赋值层级
     */
    private fun setInitList(list:List<ScoreItem>,level: Int,isShowResult:Boolean){
        for (item in list){
            item.level=level
            item.isResultShow=isShowResult
            if (!item.childScores.isNullOrEmpty()){
                item.label= getParentTotalLabel(item.childScores)
                item.score= getParentTotalScore(item.childScores)
                item.result= getItemScoreResult(item)
                setInitList(item.childScores,item.level+1,isShowResult)
            }
        }
    }

    /**
     * 轮询赋值父节点总分以及result
     */
    private fun getParentTotalScore(list: MutableList<ScoreItem>): Double {
        var total = 0.0
        for (item in list) {
            if (!item.childScores.isNullOrEmpty()) {
                item.label = getParentTotalLabel(item.childScores)
                item.score = getParentTotalScore(item.childScores)
                item.result = getItemScoreResult(item)
                total += item.score
            } else {
                item.result = getItemScoreResult(item)
                total += item.score
            }
        }
        return total
    }

    /**
     * 轮询赋值父节点总label
     */
    private fun getParentTotalLabel(list: MutableList<ScoreItem>): Double {
        var total = 0.0
        for (item in list) {
            if (!item.childScores.isNullOrEmpty()) {
                item.label = getParentTotalLabel(item.childScores)
                total += item.label
            } else {
                total += item.label
            }
        }
        return total
    }


    /**
     * 题目分数多级树列表转成模板级数
     */
    fun jsonListToModuleList(correctModule: Int, list: MutableList<ScoreItem>): MutableList<ScoreItem> {
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
                    if (!item.childScores.isNullOrEmpty()) {
                        val childItems = mutableListOf<ScoreItem>()
                        for (childItem in item.childScores) {
                            childItem.sortStr=" ${childItem.sort+1}"
                            childItems.add(childItem)
                        }
                        item.childScores = childItems
                    }
                    items.add(item)
                }
            }
            5 -> {
                for (item in list) {
                    item.sortStr="${item.sort+1}"
                    item.rootSort=item.sort
                    if (!item.childScores.isNullOrEmpty()){
                        val childItems = mutableListOf<ScoreItem>()
                        //处理当前级数据如果有第3级则显示sortStr
                        val isShowSortStr= isChildItem(item.childScores)
                        for (childItem in item.childScores) {
                            childItem.rootSort = item.rootSort
                            if (isShowSortStr){
                                if (childItem.childScores.isNullOrEmpty()) {
                                    childItem.sortStr="(${childItem.sort+1})"
                                    childItems.add(childItem)
                                } else {
                                    childItems.addAll(setRecursionChildList(correctModule,childItem.childScores, childItem))
                                }
                            }
                            else{
                                childItems.add(childItem)
                            }
                        }
                        item.childScores = childItems
                    }
                    items.add(item)
                }
            }
            6,7->{
                for (item in list) {
                    item.sortStr=ToolUtils.numbers[item.sort+1]
                    item.rootSort=item.sort
                    val parentItems = mutableListOf<ScoreItem>()
                    if (!item.childScores.isNullOrEmpty()) {
                        for (parentItem in item.childScores) {
                            parentItem.rootSort = item.rootSort
                            parentItem.level=1
                            parentItem.sortStr=" ${parentItem.sort+1}"
                            if (!parentItem.childScores.isNullOrEmpty())  {
                                val childItems = mutableListOf<ScoreItem>()
                                //处理当前级数据如果有第4级则显示sortStr
                                val isShowSortStr= isChildItem(parentItem.childScores)
                                for (childItem in parentItem.childScores){
                                    childItem.rootSort = parentItem.rootSort
                                    childItem.level=2
                                    if (isShowSortStr){
                                        if (childItem.childScores.isNullOrEmpty()) {
                                            childItem.sortStr="(${childItem.sort+1})"
                                            childItems.add(childItem)
                                        } else {
                                            childItems.addAll(setRecursionChildList(correctModule,childItem.childScores, childItem))
                                        }
                                    }
                                    else{
                                        childItems.add(childItem)
                                    }
                                }
                                parentItem.childScores=childItems
                            }
                            parentItems.add(parentItem)
                        }
                        item.childScores=parentItems
                    }
                    items.add(item)
                }
            }
        }
        return items
    }


    /**
     * 判断当前层级是否有子集
     */
    private fun isChildItem(list: MutableList<ScoreItem>):Boolean{
        var isShowSortStr=false
        for (childItem in list){
            if (!childItem.childScores.isNullOrEmpty()){
                isShowSortStr=true
            }
        }
        return isShowSortStr
    }

    /**
     * 更新初始数据
     */
    fun updateInitListData(initList: MutableList<ScoreItem>, currentList: MutableList<ScoreItem>, correctModule: Int): MutableList<ScoreItem> {
        when (correctModule) {
            1, 2,3,4 -> {
                return currentList
            }
            5,6,7->{
                val items= setRecursionList(currentList)
                setRecursionListAssignScore(initList,items)
                setInitList(initList,0,false)
            }
        }
        return initList
    }

    /**
     * 遍历全部数据
     */
    private fun setRecursionList(list: MutableList<ScoreItem>):MutableList<ScoreItem>{
        val items = mutableListOf<ScoreItem>()
        for (item in list) {
            if (item.childScores.isNullOrEmpty()) {
                items.add(item)
            } else {
                items.addAll(setRecursionList(item.childScores))
            }
        }
        return items
    }

    /**
     * 递归遍历查找所有子题目
     */
    private fun setRecursionChildList(correctModule: Int, list: MutableList<ScoreItem>, parentItem: ScoreItem): MutableList<ScoreItem> {
        val items = mutableListOf<ScoreItem>()
        for (item in list) {
            item.rootSort = parentItem.rootSort
            item.sortStr=if (list.indexOf(item)==0&&item.level==if (correctModule==5)2 else 3) "(${parentItem.sort+1})" else ""
            if (item.childScores.isNullOrEmpty()) {
                items.add(item)
            } else {
                items.addAll(setRecursionChildList(correctModule,item.childScores, item))
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

    /**
     * 获取当前题目分数
     */
    private fun getCurrentScore(currentItem: ScoreItem, scoreList: MutableList<ScoreItem>): Double {
        for (item in scoreList) {
            if (item.level == currentItem.level && item.rootSort == currentItem.rootSort && item.sort == currentItem.sort) {
                return item.score
            }
        }
        return 0.0
    }
}