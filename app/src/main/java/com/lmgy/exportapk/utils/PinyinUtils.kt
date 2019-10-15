package com.lmgy.exportapk.utils

import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination

/**
 * @author lmgy
 * @date 2019/10/13
 */
object PinyinUtils {

    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     *
     * @param inputString
     * @return
     */
    fun getPinYin(inputString: String): String {
        val format = HanyuPinyinOutputFormat()
        format.caseType = HanyuPinyinCaseType.LOWERCASE
        format.toneType = HanyuPinyinToneType.WITHOUT_TONE
        format.vCharType = HanyuPinyinVCharType.WITH_V

        val input = inputString.trim { it <= ' ' }.toCharArray()
        var output = ""

        try {
            for (c in input) {
                if (c.toString().matches("[\\u4E00-\\u9FA5]+".toRegex())) {
                    val temp = PinyinHelper.toHanyuPinyinStringArray(c, format)
                    run { output += temp[0] }
                } else {
                    output += c.toString()
                }
            }
        } catch (e: BadHanyuPinyinOutputFormatCombination) {
            e.printStackTrace()
        } catch (npex: NullPointerException) {
            npex.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return output
    }

    /**
     * 获取汉字串拼音首字母，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音首字母
     */
    fun getFirstSpell(chinese: String): String {
        val pybf = StringBuffer()
        val arr = chinese.toCharArray()
        val defaultFormat = HanyuPinyinOutputFormat()
        defaultFormat.caseType = HanyuPinyinCaseType.LOWERCASE
        defaultFormat.toneType = HanyuPinyinToneType.WITHOUT_TONE
        try {
            for (c in arr) {
                if (c.toInt() > 128) {
                    val temp = PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat)
                    if (temp != null) {
                        pybf.append(temp[0][0])
                    }

                } else {
                    pybf.append(c)
                }
            }
        } catch (bhpe: BadHanyuPinyinOutputFormatCombination) {
            bhpe.printStackTrace()
        } catch (npex: NullPointerException) {
            npex.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return pybf.toString().replace("\\W".toRegex(), "").trim { it <= ' ' }
    }

    /**
     * 获取汉字串拼音，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音
     */
    fun getFullSpell(chinese: String): String {
        val pybf = StringBuffer()
        val arr = chinese.toCharArray()
        val defaultFormat = HanyuPinyinOutputFormat()
        defaultFormat.caseType = HanyuPinyinCaseType.LOWERCASE
        defaultFormat.toneType = HanyuPinyinToneType.WITHOUT_TONE
        try {
            for (c in arr) {
                if (c.toInt() > 128) {
                    pybf.append(PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat)[0])
                } else {
                    pybf.append(c)
                }
            }
        } catch (e: BadHanyuPinyinOutputFormatCombination) {
            e.printStackTrace()
        } catch (npex: NullPointerException) {
            npex.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return pybf.toString()
    }

}
