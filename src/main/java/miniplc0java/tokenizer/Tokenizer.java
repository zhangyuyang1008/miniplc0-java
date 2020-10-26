package miniplc0java.tokenizer;

//尝试修改
import miniplc0java.error.TokenizeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

import static miniplc0java.error.ErrorCode.IntegerOverflow;

public class Tokenizer {

    private StringIter it;

    String token="";

    public Tokenizer(StringIter it) {
        this.it = it;
    }

    // 这里本来是想实现 Iterator<Token> 的，但是 Iterator 不允许抛异常，于是就这样了
    /**
     * 获取下一个 Token
     *
     * @return
     * @throws TokenizeError 如果解析有异常则抛出
     */
    public Token nextToken() throws TokenizeError {
        it.readAll();

        // 跳过之前的所有空白字符
        skipSpaceCharacters();

        if (it.isEOF()) {
            return new Token(TokenType.EOF, "", it.currentPos(), it.currentPos());
        }

        char peek = it.peekChar();
        if (Character.isDigit(peek)) {
            return lexUInt();
        } else if (Character.isAlphabetic(peek)) {
            return lexIdentOrKeyword();
        } else {
            return lexOperatorOrUnknown();
        }
    }

    private Token lexUInt() throws TokenizeError {

        token="";
        //记录初始位置
        Pos p=it.ptr;
        char charNow = it.nextChar();
        // 将字符串和字符连接起来
        token+=charNow;
        // 查看下一个字符 但是不移动指针
        charNow = it.peekChar();
        while(Character.isDigit(charNow)){
            // 前进一个字符，并存储这个字符
            charNow=it.nextChar();
            // 将字符串和字符连接起来
            token+=charNow;
            // 查看下一个字符 但是不移动指针
            charNow = it.peekChar();
        }
        // 请填空：
        // 解析存储的字符串为无符号整数
        try{
            Integer num = Integer.parseInt(token);
            return new Token(TokenType.Uint, num,p, it.ptr);
        }
        catch (Exception e){
            throw new TokenizeError(ErrorCode.IntegerOverflow,p);
        }
        // 解析成功则返回无符号整数类型的token，否则返回编译错误
        //
        // Token 的 Value 应填写数字的值
    }

    private Token lexIdentOrKeyword() throws TokenizeError {

        token="";
        //记录初始位置
        Pos p=it.ptr;
        // 前进一个字符，并存储这个字符
        char charNow = it.nextChar();
        // 将字符串和字符连接起来
        token+=charNow;

        // 请填空：
        // 直到查看下一个字符不是数字或字母为止:
        charNow = it.peekChar();
        while(Character.isDigit(charNow) || Character.isAlphabetic(charNow)){
            // 前进一个字符，并存储这个字符
            charNow=it.nextChar();
            // 将字符串和字符连接起来
            token+=charNow;
            // 查看下一个字符 但是不移动指针
            charNow = it.peekChar();
        }
        //
        // 尝试将存储的字符串解释为关键字
        // -- 如果是关键字，则返回关键字类型的 token
        // -- 否则，返回标识符
        // Token 的 Value 应填写标识符或关键字的字符串
        try{
            if(token.toLowerCase().equals("begin")){
                return new Token(TokenType.Begin, "begin", p, it.ptr);
            }
            else if (token.toLowerCase().equals("end")){
                return new Token(TokenType.End, "end", p, it.ptr);
            }
            else if (token.toLowerCase().equals("var")){
                return new Token(TokenType.Var, "var", p, it.ptr);
            }
            else if (token.toLowerCase().equals("const")){
                return new Token(TokenType.Const, "const", p, it.ptr);
            }
            else if (token.toLowerCase().equals("print")){
                return new Token(TokenType.Print, "print", p, it.ptr);

            }
            else {
                return new Token(TokenType.Ident, token, p, it.ptr);
            }
        }
        catch (Exception e){
            throw new TokenizeError(ErrorCode.InvalidInput,it.ptr);
        }
    }

    private Token lexOperatorOrUnknown() throws TokenizeError {

        switch (it.nextChar()) {
            case '+':
                try{
                    return new Token(TokenType.Plus, '+', it.previousPos(), it.currentPos());
                }catch (Exception e){
                    throw new TokenizeError(ErrorCode.ExpectedToken,it.previousPos());
                }

            case '-':
                try{
                    return new Token(TokenType.Minus, '-', it.previousPos(), it.currentPos());
                }catch (Exception e){
                    throw new TokenizeError(ErrorCode.ExpectedToken,it.previousPos());
                }

            case '*':
                try{
                    return new Token(TokenType.Mult, '*', it.previousPos(), it.currentPos());
                }catch (Exception e){
                    throw new TokenizeError(ErrorCode.ExpectedToken,it.previousPos());
                }

            case '/':
                try{
                    return new Token(TokenType.Div, '/', it.previousPos(), it.currentPos());
                }catch (Exception e){
                    throw new TokenizeError(ErrorCode.ExpectedToken,it.previousPos());
                }
            case '=':
                try{
                    return new Token(TokenType.Equal, '=', it.previousPos(), it.currentPos());
                }catch (Exception e){
                    throw new TokenizeError(ErrorCode.ExpectedToken,it.previousPos());
                }
            case ';':
                try{
                    return new Token(TokenType.Semicolon, ';', it.previousPos(), it.currentPos());
                }catch (Exception e){
                    throw new TokenizeError(ErrorCode.ExpectedToken,it.previousPos());
                }
            case '(':
                try{
                    return new Token(TokenType.LParen, '(', it.previousPos(), it.currentPos());
                }catch (Exception e){
                    throw new TokenizeError(ErrorCode.ExpectedToken,it.previousPos());
                }
            case ')':
                try{
                    return new Token(TokenType.RParen, ')', it.previousPos(), it.currentPos());
                }catch (Exception e){
                    throw new TokenizeError(ErrorCode.ExpectedToken,it.previousPos());
                }

            default:
                // 不认识这个输入，摸了
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }



    private void skipSpaceCharacters() {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }
}
