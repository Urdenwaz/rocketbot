public class Prompt {

    private String prompt;

    public Prompt(String prompt) {
        this.prompt = prompt;
        System.out.println("Prompt " + prompt + " loaded");
    }

    public boolean isPrompt(String prompt) {
        return this.prompt.equalsIgnoreCase(prompt.trim());
    }

    public String getPrompt() {
        String promptFull = "";
        if (prompt.equalsIgnoreCase("twe")) {
            promptFull = "To what extent do you agree with this statement?";
        }
        if (prompt.equalsIgnoreCase("disc")) {
            promptFull = "Discuss.";
        }
        if (prompt.equalsIgnoreCase("eval")) {
            promptFull = "Evaluate.";
        }
        return promptFull;
    }

}
