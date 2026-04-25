package temp;

public abstract class Chain {
    private Chain nextChain;
    public Chain(){}

    private void setNextChain(Chain nextChain){
        this.nextChain = nextChain;
    }
    public static Chain link(Chain first, Chain... chains) {
        Chain head = first;
        for (Chain chain : chains) {
            first.setNextChain(chain);
            first = chain;
        }
        return head;
    }
    public abstract boolean check();
    protected boolean checknext(){
        if(nextChain == null){
            return true;
        }
        return nextChain.check();
    }
}
