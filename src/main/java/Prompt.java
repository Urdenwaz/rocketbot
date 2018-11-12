public class Prompt {

    private String prompt;

    public Prompt(String prompt) {
        this.prompt = prompt;
    }

    public boolean isPrompt(String prompt) {
        return this.prompt.equalsIgnoreCase(prompt.trim());
    }

    public String getPrompt() {
        if (prompt.equalsIgnoreCase("twe")) {
            prompt = "To what extent do you agree with this statement?";
        }
        if (prompt.equalsIgnoreCase("disc")) {
            prompt = "Discuss.";
        }
        if (prompt.equalsIgnoreCase("eval")) {
            prompt = "Evaluate.";
        }
        return prompt;
    }

}
