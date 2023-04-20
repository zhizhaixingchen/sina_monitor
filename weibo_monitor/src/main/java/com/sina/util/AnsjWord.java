package com.sina.util;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnsjWord {
    public static List<String> nlpWord(String str) {
        List<String> list = new ArrayList<>();
        //只关注这些词性的词
        Set<String> expectedNature = new HashSet<String>() {{
            add("n");add("nr");;add("ns");add("mq");add("q");add("j");add("vn");
            add("nt");add("nz");add("nw");add("ng");add("wh");add("en");add("mm");
        }};
        Result result = NlpAnalysis.parse(str); //分词结果的一个封装，主要是一个List<Term>的terms
        List<Term> terms = result.getTerms(); //拿到terms
        for(int i=0; i<terms.size(); i++) {
            String word = terms.get(i).getName(); //拿到词
            String natureStr = terms.get(i).getNatureStr(); //拿到词性
            if(expectedNature.contains(natureStr)) {
                //不需要单个组成的词语
                if(word.length()!=1){
                    list.add(word);
                }
            }
        }
        return list;
    }
}