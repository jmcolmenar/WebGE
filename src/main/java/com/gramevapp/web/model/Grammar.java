package com.gramevapp.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name="grammar")
public class Grammar {

    @Id
    @Column(name = "GRAMMAR_ID", nullable = false, updatable= false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "grammar_list",
            joinColumns = {
                    @JoinColumn(name = "GRAMMAR_ID", nullable = false)
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "EXPERIMENT_ID", referencedColumnName = "EXPERIMENT_ID")
            }
    )
    private Experiment experimentId;

    @Column
    private Long runId;

    @Column
    private String grammarName;

    @Column
    private String grammarDescription;

    @Column(columnDefinition = "TEXT") // https://stackoverflow.com/questions/31833337/hibernate-could-not-execute-statement-sql-n-a-saving-nested-object
    private String fileText; // This is the text on the file - That's written in a areaText - So we can take it as a String

    public Grammar(){
        experimentId = new Experiment();
    }

    /**
     * Copy constructor.
     */
    public Grammar(Grammar grammar) {
        this(grammar.getExperimentId(), grammar.getRunId(), grammar.getGrammarName(), grammar.getGrammarDescription(), grammar.getFileText());
        //no defensive copies are created here, since
        //there are no mutable object fields (String is immutable)
    }

    public Grammar(Experiment experimentId, Long runId, String grammarName, String grammarDescription, String fileText) {
        this.experimentId = experimentId;
        this.runId = runId;
        this.grammarName = grammarName;
        this.grammarDescription = grammarDescription;
        this.fileText = fileText;
    }

    public Grammar(String grammarName, String grammarDescription, String fileText) {
        this.grammarName = grammarName;
        this.grammarDescription = grammarDescription;
        this.fileText = fileText;

        experimentId = new Experiment();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Experiment getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(Experiment experimentId) {
        this.experimentId = experimentId;
    }

    public String getGrammarName() {
        return grammarName;
    }

    public void setGrammarName(String grammarName) {
        this.grammarName = grammarName;
    }

    public String getGrammarDescription() {
        return grammarDescription;
    }

    public void setGrammarDescription(String grammarDescription) {
        this.grammarDescription = grammarDescription;
    }

    public String getFileText() {
        return fileText;
    }

    public void setFileText(String fileText) {
        this.fileText = fileText;
    }

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }
}
