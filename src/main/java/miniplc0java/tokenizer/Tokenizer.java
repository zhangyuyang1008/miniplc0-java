package miniplc0java.tokenizer;

import miniplc0java.error.TokenizeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

public class Tokenizer {

    private StringIter it;
    String str_token="";
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

        // 查看下一个字符 但是不移动指针
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
        // 直到查看下一个字符不是数字为止
        // 清空字符串
        str_token="";
        //记录初始位置
        Pos p1=it.ptr;
        // 前进一个字符，并存储这个字符
        char peek = it.nextChar();
        // 将字符串和字符连接起来
        str_token+=peek;
        // 查看下一个字符 但是不移动指针
        peek = it.peekChar();
        while(Character.isDigit(peek)){
            // 前进一个字符，并存储这个字符
            peek=it.nextChar();
            // 将字符串和字符连接起来
            str_token+=peek;
            // 查看下一个字符 但是不移动指针
            peek = it.peekChar();
        }
        try{
            // 解析存储的字符串为无符号整数
            Integer num=Integer.parseInt(str_token);
            // 解析成功则返回无符号整数类型的token，否则返回编译错误
            return new Token(TokenType.Uint,num,p1,it.ptr);
        }catch(Exception e){
            // Token 的 Value 应填写数字的值
            throw new TokenizeError(ErrorCode.ExpectedToken,p1);
        }
    }

    private Token lexIdentOrKeyword() throws TokenizeError {
        // 直到查看下一个字符不是数字或字母为止
        // 清空字符串
        str_token="";
        //记录初始位置
        Pos p1=it.ptr;
        // 前进一个字符，并存储这个字符
        char peek = it.nextChar();
        // 将字符串和字符连接起来
        str_token+=peek;
        // 查看下一个字符 但是不移动指针
        peek = it.peekChar();
        while(Character.isDigit(peek) || Character.isAlphabetic(peek)){
            // 前进一个字符，并存储这个字符
            peek=it.nextChar();
            // 将字符串和字符连接起来
            str_token+=peek;
            // 查看下一个字符 但是不移动指针
            peek = it.peekChar();
        }
        try{
            // 尝试将存储的字符串解释为关键字
            // 如果是关键字，则返回关键字类型的token
            if(isKeepWord(str_token)!=null)
                return new Token(isKeepWord(str_token),isKeepWord(str_token).toString().toLowerCase(),p1,it.ptr);
                // 否则，返回标识符
            else return new Token(TokenType.Ident,str_token,p1,it.ptr);
        }catch(Exception e){
            // Token 的 Value 应填写标识符或关键字的字符串
            throw new TokenizeError(ErrorCode.ExpectedToken,p1);
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

    // 判断是否为保留字
    private TokenType isKeepWord(String str){
        if(str.toLowerCase().equals(TokenType.Begin.toString().toLowerCase()))
            return TokenType.Begin;
        else if(str.toLowerCase().equals(TokenType.End.toString().toLowerCase()))
            return TokenType.End;
        else if(str.toLowerCase().equals(TokenType.Var.toString().toLowerCase()))
            return TokenType.Var;
        else if(str.toLowerCase().equals(TokenType.Const.toString().toLowerCase()))
            return TokenType.Const;
        else if(str.toLowerCase().equals(TokenType.Print.toString().toLowerCase()))
            return TokenType.Print;
        else return null;
    };
}
